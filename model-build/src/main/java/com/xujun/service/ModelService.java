package com.xujun.service;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xujun.config.CommonEnvConfig;
import com.xujun.dao.ModelMapper;
import com.xujun.model.JobInformation;
import com.xujun.model.ModelInformation;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.utils.FileUtils;
import com.xujun.utils.HdfsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ModelService {

    private static Logger logger = LoggerFactory.getLogger(ModelService.class);

    @Autowired
    ModelMapper modelMapper;

    @Value("${buildmodel.path}")
    private String localPath;



    public Result getModelPage(String name, Integer offset, Integer limit){
        PageHelper.startPage(offset, limit, true);
        List<ModelInformation> modelInformationList = modelMapper.selectModelInformationByModelNamePage(name);
        PageInfo<ModelInformation> modelInformationPageInfo = new PageInfo<>(modelInformationList);
        JSONObject rs = new JSONObject();
        rs.put("list", modelInformationList);
        rs.put("total", modelInformationPageInfo.getTotal());
        rs.put("offset", offset);
        rs.put("limit", limit);
        return Result.success(rs);
    }

    public Result deleteModelById(Integer modelId){
        ModelInformation modelInformation = modelMapper.selectModelInformationByModelId(modelId);
        if(modelInformation != null){
            logger.info("modelInformation is exist.");
        }else{
            logger.error(String.format("modelInformation does not exist，dataId is [%s]", modelId.toString()));
            return Result.result(ResultCode.DATA_NOT_EXIST_IN_MYSQL);
        }
        try {
            HdfsUtils.deleteHdfsData(modelInformation.getHdfsPath());
            logger.info(String.format("Hdfs data deleted successfully, hdfsPath is [%s]", modelInformation.getHdfsPath()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Hdfs data deleted failed, hdfsPath is [%s]", modelInformation.getHdfsPath()));
            return Result.result(ResultCode.DELETE_HDFS_FAILED, e.getMessage());
        }
        Integer delCount = modelMapper.deleteModelInformationByModelId(modelId);
        if(delCount > 0){
            logger.info(String.format("Delete modelInformation id:[%s] successfully", modelId.toString()));
        }else{
            logger.error(String.format("Delete modelInformation id:[%s] failed", modelId.toString()));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        return Result.success();

    }

    public Object downloadModelById(Integer modelId){
        ModelInformation modelInformation = modelMapper.selectModelInformationByModelId(modelId);
        if(modelInformation != null){
            logger.info("modelInformation is exist.");
        }else{
            logger.error(String.format("modelInformation does not exist，dataId is [%s]", modelId.toString()));
            return Result.result(ResultCode.DATA_NOT_EXIST_IN_MYSQL);
        }
        String hdfsPath = modelInformation.getHdfsPath();
        String tempLocalPath = Paths.get(localPath, "temp_path", HdfsUtils.getFileNameFromHdfsUrl(hdfsPath)).toString();


        try {
            HdfsUtils.copyFileFromHdfs(hdfsPath, tempLocalPath);
            logger.info("hdfs to local successfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("hdfs to local failed");
            return Result.result(ResultCode.DOWNLOAD_HDFS_FAILED);
        }

        try {
            return fileToResponseEntity(new File(tempLocalPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("fileToResponseEntity failed");
            return Result.failure("fileToResponseEntity failed");
        }
    }

    private Object fileToResponseEntity(File file) throws IOException {

        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", resource.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
