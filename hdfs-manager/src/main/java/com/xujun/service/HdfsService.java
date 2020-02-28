package com.xujun.service;

import com.xujun.config.CommonEnvConfig;
import com.xujun.model.DataInformation;
import com.xujun.model.JobInformation;
import com.xujun.utils.FileUtils;
import com.xujun.utils.HdfsUtils;
import com.xujun.utils.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
public class HdfsService {
    private static Logger logger = LoggerFactory.getLogger(HdfsService.class);

    @Value("${hdfsmanager.temp_path}")
    private String localPath;

    public DataInformation dataProcessHdfsToHdfs(String src, String dst, DataInformation dataInformation){
        String tpLocalPath = Paths.get(localPath, UuidUtils.createUUID()).toString();
        File tpLocalFile = new File(tpLocalPath);
        if(!tpLocalFile.exists()){
            tpLocalFile.mkdirs();
        }

        String filetpLocalPath = Paths.get(tpLocalPath, dataInformation.getDataName()).toString();
        File filetpLocalFile = new File(filetpLocalPath);
        try {
            HdfsUtils.copyFileFromHdfs(src,filetpLocalPath);
            logger.info("first hdfs to local successfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("first hdfs to local failed");
            dataInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            return dataInformation;
        }
        dataInformation.setLength(FileUtils.getLineNumber(filetpLocalFile));
        dataInformation.setCapacity(FileUtils.getFileSizeByKB(filetpLocalFile));
        try {
            HdfsUtils.copyFromLocalFile(filetpLocalPath, dst);
            logger.info(String.format("Writing file to hdfs，save at [%s]", dst));
            dataInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Writing file to hdfs failed，save at [%s]", dst));
            dataInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            return dataInformation;
        }
        if(filetpLocalFile.delete()){
            logger.info("delete tplocal file successfully");
        }else{
            logger.error("delete tp local file failed");
            return dataInformation;
        }
        return dataInformation;
    }

    public JobInformation buildModelHdfsToLocal(String src, String dst, JobInformation jobInformation){
        File dstFile = new File(dst);
        try {
            HdfsUtils.copyFileFromHdfs(src,dst);
            logger.info("hdfs to local successfully");
            jobInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_RUNNING);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("hdfs to local failed");
            jobInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            return jobInformation;
        }
        jobInformation.setDataLength(jobInformation.getDataLength() + FileUtils.getLineNumber(dstFile));
        jobInformation.setDataShardReadySum(jobInformation.getDataShardReadySum() + 1);
        return jobInformation;
    }
}
