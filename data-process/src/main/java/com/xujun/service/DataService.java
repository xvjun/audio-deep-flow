package com.xujun.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xujun.config.CommonEnvConfig;
import com.xujun.config.EnvConfig;
import com.xujun.dao.DataMapper;
import com.xujun.kafka.beans.Message;
import com.xujun.kafka.provider.KafkaSender;
import com.xujun.model.DataInformation;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.utils.FileUtils;
import com.xujun.utils.HdfsUtils;
import com.xujun.utils.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private static Logger logger = LoggerFactory.getLogger(DataService.class);

    @Value("${dataprocess.temp_path}")
    private String path;

    @Value("${dataprocess.hdfsUrl}")
    private String hdfsUrl;

    @Autowired
    DataMapper dataMapper;

    @Autowired
    KafkaSender kafkaSender;

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);
    /**
     * 数据临时落盘，写入hdfs，记录到mysql
     * @return
     */


    public Result processLocalFormatData(MultipartFile formatData){
        String namespace = UuidUtils.createUUID();
        long ts = System.currentTimeMillis();

        String fileName = ts+"_"+formatData.getOriginalFilename();
        String dst = Paths.get(path, namespace, EnvConfig.NOPROCESSNETSAVEFILE_DIR).toString();

        File dstFile = new File(dst);
        if(!dstFile.exists()){
            dstFile.mkdirs();
        }
        String filedst = Paths.get(dst, fileName).toString();
        File filedstFile = new File(filedst);
        try {
            formatData.transferTo(filedstFile);
            logger.info("{} uploaded", filedst);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to write {}", filedst);
            return Result.result(ResultCode.UPLOAD_CODE_FAILED, e.getMessage());
        }
        String hdfsFilePath = hdfsUrl + Paths.get(CommonEnvConfig.DATA_HDFS_DIR).toString();
        try {
            HdfsUtils.copyFromLocalFile(filedst, hdfsFilePath);
            logger.info(String.format("Write file to hdfs，save at [%s]", hdfsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Write file to hdfs failed，save at [%s]", hdfsFilePath));
            return Result.result(ResultCode.UPLOAD_HDFS_FAILED, e.getMessage());
        }
        //记录到mysql

        String importTime = df.format(new Date(ts));
        String hdfsPath = hdfsFilePath + "/" + fileName;
        DataInformation information = new DataInformation();
        information.setDataId(0);
        information.setDataName(fileName);
        information.setHdfsPath(hdfsPath);
        information.setLength(FileUtils.getLineNumber(filedstFile));
        information.setCapacity(FileUtils.getFileSizeByKB(filedstFile));
        information.setImportTime(importTime);
        information.setIsCompleted(CommonEnvConfig.IS_COMPLETED_SUCCESS);
        Integer addCount = dataMapper.addDataInfomation(information);
        if(addCount > 0){
            logger.info("Mysql dataInformation updated successfully");
        }else{
            logger.error(String.format("Mysql dataInformation updated failed, dataInformation is [%s]", information));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        if (filedstFile.delete()) {
            logger.info("delete temp local file sucessfully");
        }else{
            logger.error("delete temp local file failed");
        }
        return Result.success();
    }

    //
    // 通过kafka将任务提交到hdfs-manager,记录到mysql
    public Result processHdfsFormatData(String inputHdfsUrl){
        long ts = System.currentTimeMillis();

        String fileName = ts+"_"+HdfsUtils.getFileNameFromHdfsUrl(inputHdfsUrl);
        String dstHdfsFilePath = hdfsUrl + Paths.get(CommonEnvConfig.DATA_HDFS_DIR).toString();

        String hdfsPath = dstHdfsFilePath+"/"+fileName;
        String importTime = df.format(new Date(ts));
        DataInformation information = new DataInformation();
        information.setDataId(0);
        information.setDataName(fileName);
        information.setHdfsPath(hdfsPath);
        information.setLength(0l);
        information.setCapacity(0l);
        information.setImportTime(importTime);
        information.setIsCompleted(CommonEnvConfig.IS_COMPLETED_UPLOADING);

        Integer addCount = dataMapper.addDataInfomation(information);

        if(addCount > 0){
            logger.info("Mysql dataInformation insert successfully");
        }else{
            logger.error(String.format("Mysql dataInformation insert failed, dataInformation is [%s]", information));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        Message message = new Message();
        message.setModel(CommonEnvConfig.DATA_PROCESS_HDFS_HDFS_KAFKA_MODEL);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(inputHdfsUrl, dstHdfsFilePath);
        message.setDataMap(dataMap);
//        message.setSrcPath(inputHdfsUrl);
//        message.setDstPath(dstHdfsFilePath);
        message.setObject(information);
        if (!kafkaSender.send(message)) {
            information.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            Integer updateCount = dataMapper.updateDataInfomationByDataId(information);
            if(updateCount > 0){
                logger.warn("Mysql dataInformation kafka send failed,update successfully");
            }else{
                logger.error(String.format("Mysql dataInformation callback kafka send failed, dataInformation is [%s]", information));
                return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
            }
            return Result.result(ResultCode.KAFKA_SEND_FAILED);
        }
        return Result.result(ResultCode.WAITING_DATA_UPLOAD);
    }

    // 用于kafka的回调，更新mysql的信息
    public Result kafkaCallBack(Message message){
        DataInformation dataInformation = JSONObject.parseObject(message.getObject().toString(), DataInformation.class);
        Integer updateCount = dataMapper.updateDataInfomationByDataId(dataInformation);
        if(updateCount > 0){
            logger.info("Mysql dataInformation updated successfully");
        }else{
            logger.error(String.format("Mysql dataInformation updated failed, dataInformation is [%s]", dataInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }

        return Result.success();
    }

    public Result getDataPage(String name, Integer offset, Integer limit){
        PageHelper.startPage(offset, limit, true);
        List<DataInformation> dataInformationList = dataMapper.selectDataInformationPage(name);
        PageInfo<DataInformation> dataInformationPageInfo = new PageInfo<>(dataInformationList);
        JSONObject rs = new JSONObject();
        rs.put("list", dataInformationList);
        rs.put("total", dataInformationPageInfo.getTotal());
        rs.put("offset", offset);
        rs.put("limit", limit);
        return Result.success(rs);
    }


    public Result deleteDataById(Integer dataId){
        DataInformation dataInformation = dataMapper.selectDataInformationByDataId(dataId);
        if(dataInformation != null){
            logger.info("datainformation is exist.");
        }else{
            logger.error(String.format("dataInformation does not exist，dataId is [%s]", dataId.toString()));
            return Result.result(ResultCode.DATA_NOT_EXIST_IN_MYSQL);
        }
        try {
            HdfsUtils.deleteHdfsData(dataInformation.getHdfsPath());
            logger.info(String.format("Hdfs data deleted successfully, hdfsPath is [%s]", dataInformation.getHdfsPath()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Hdfs data deleted failed, hdfsPath is [%s]", dataInformation.getHdfsPath()));
            return Result.result(ResultCode.DELETE_HDFS_FAILED, e.getMessage());
        }
        Integer delCount = dataMapper.deleteDataInformationByDataId(dataId);
        if(delCount > 0){
            logger.info(String.format("Delete dataInformation id:[%s] successfully", dataId.toString()));
        }else{
            logger.error(String.format("Delete dataInformation id:[%s] failed", dataId.toString()));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        return Result.success();
    }

}
