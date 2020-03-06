package com.xujun.service;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.xujun.config.CommonEnvConfig;
import com.xujun.dao.ServingMapper;
import com.xujun.kafka.beans.Message;
import com.xujun.kafka.provider.KafkaSender;
import com.xujun.model.ModelInformation;
import com.xujun.model.ServingInformation;
import com.xujun.model.req.CreateServingRequest;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.utils.HdfsUtils;
import com.xujun.utils.TarUtils;
import com.xujun.utils.UuidUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServingService {

    @Value("${servingapp.path}")
    private String localPath;

    private static Logger logger = LoggerFactory.getLogger(ServingService.class);

    @Autowired
    ServingMapper servingMapper;

    @Autowired
    KafkaSender kafkaSender;

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);

    @Value("${audio-deep-flow.k8s.namespace}")
    private String k8sNamespace;

    @Value("${audio-deep-flow.servingapp.job.image}")
    private String image;

    @Autowired
    private K8sService k8sService;



    /**
     * 创建目录，写mysql，发kafka
     * 下载model，
     * 回调：解压，创建deploy
     * @param request
     * @return
     */
    public Result createServing(CreateServingRequest request){
        String servingLocalPath = Paths.get(localPath, UuidUtils.createUUID()).toString();
        String modelLocalPath = Paths.get(servingLocalPath,
                HdfsUtils.getFileNameFromHdfsUrl(request.getModelHdfsPath())).toString();
        File servingLocalPathFile = new File(servingLocalPath);
        if(!servingLocalPathFile.exists()){
            servingLocalPathFile.mkdirs();
        }

        long ts = System.currentTimeMillis();
        String startTime = df.format(new Date(ts));

        ServingInformation servingInformation = new ServingInformation();
        servingInformation.setServingId(0);
        servingInformation.setServingName("serving-" + ts);
        servingInformation.setCpu(request.getCpu());
        servingInformation.setMemory(request.getMemory());
        servingInformation.setInstance(request.getInstance());
        servingInformation.setModelLocalPath(modelLocalPath);
        servingInformation.setStartTime(startTime);
        servingInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_UPLOADING);

        int addCount = servingMapper.addServingInformation(servingInformation);
        if(addCount > 0){
            logger.info("Mysql servingInformation insert successfully");
        }else{
            logger.error(String.format("Mysql servingInformation insert failed, servingInformation is [%s]", servingInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }


        //kafka
        Message message = new Message();
        message.setModel(CommonEnvConfig.SERVING_APP_HDFS_LOCAL_KAFKA_MODEL);
        Map<String, String> dataMap = new HashMap<>();
        String localPath = Paths.get(servingLocalPath, HdfsUtils.getFileNameFromHdfsUrl(request.getModelHdfsPath())).toString();
        dataMap.put(request.getModelHdfsPath(), localPath);
        message.setDataMap(dataMap);
        message.setObject(servingInformation);
        if (kafkaSender.send(message)) {
            logger.info("kafka send successfully");
        }else{
            logger.error("kafka send failed");
            servingInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            int updateCount = servingMapper.updateServingInformationByServingId(servingInformation);
            if(updateCount > 0){
                logger.warn("Mysql servingInformation kafka send failed,update successfully");
            }else{
                logger.error(String.format("Mysql servingInformation callback kafka send failed, servingInformation is [%s]", servingInformation));
                return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
            }
            return Result.result(ResultCode.KAFKA_SEND_FAILED);
        }

        return Result.result(ResultCode.WAITING_DATA_UPLOAD);

    }

    // 回调：解压，创建deploy,mysql
    public Result kafkaCallBack(Message message){
        ServingInformation servingInformation = JSONObject.parseObject(message.getObject().toString(), ServingInformation.class);
        if(servingInformation.getIsCompleted().equals(CommonEnvConfig.IS_COMPLETED_FAILED)){
            logger.error("model download failed");

            int updateCount = servingMapper.updateServingInformationByServingId(servingInformation);
            if(updateCount > 0){
                logger.warn("Mysql servingInformation data download failed,update successfully");
            }else{
                logger.error(String.format("Mysql servingInformation data download send failed, servingInformation is [%s]", servingInformation));
                return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
            }
        }

        String localTarPath = servingInformation.getModelLocalPath();
        File localTarPathFile = new File(localTarPath);
        try {
            TarUtils.dearchive(localTarPathFile);
            logger.info("model.tar dearchive successfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("model.tar dearchive failed");
            return Result.failure(e.getMessage());
        }

        String modelPath = Paths.get(localTarPathFile.getParent(), "model").toString();


        // k8s启动svc,deploy
        String label = servingInformation.getServingName();
        String svcName = servingInformation.getServingName();

        String deployName = servingInformation.getServingName();
        Integer replicas = servingInformation.getInstance();
        Float cpu = servingInformation.getCpu();
        Integer memory = servingInformation.getMemory();

        Map<String, Object> params = new HashMap<>();

        params.put("label", label);
        params.put("svcName", svcName);
        params.put("k8sNamespace", k8sNamespace);
        params.put("deployName", deployName);
        params.put("replicas", replicas);
        params.put("modelPath", modelPath);
        params.put("cpu", cpu);
        params.put("memory", memory);
        params.put("image", image);

        Jinjava jinjava = new Jinjava();
        String svcTemplate = null;
        String deployTemplate = null;
        try {
            svcTemplate = Resources.toString(Resources.getResource("svc.yaml.template.json"), Charsets.UTF_8);
            deployTemplate = Resources.toString(Resources.getResource("deploy.yaml.template.json"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Failed to read svcTemplate or deployTemplate");
            return Result.result(ResultCode.READ_FILE_FAILED,e.getMessage());
        }
        String svcRenderedTemplate = jinjava.render(svcTemplate, params);
        String deployRenderedTemplate = jinjava.render(deployTemplate, params);

        Optional<Result> ro = k8sService.createDeploy(deployRenderedTemplate);
        if (ro.isPresent()) {
            logger.error("k8s deploy start failed");
            return ro.get();
        }else{
            logger.info("k8s deploy start successfully");
        }

        ro = k8sService.createSvc(svcRenderedTemplate);
        if (ro.isPresent()) {
            logger.error("k8s svc start failed");
            return ro.get();
        }else{
            logger.info("k8s svc start successfully");
        }


        // mysql
        servingInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_SUCCESS);
        int updateCount = servingMapper.updateServingInformationByServingId(servingInformation);
        if(updateCount > 0){
            logger.info("Mysql servingInformation update successfully");
        }else{
            logger.error(String.format("Mysql servingInformation update failed, servingInformation is [%s]", servingInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }

        return Result.success();
    }

//    删除k8s的deploy,svc，删除本地目录,删除mysql
    public Result deleteServingById(Integer servingId){
        ServingInformation servingInformation = servingMapper.selectServingInformationByServingId(servingId);
        if(servingInformation != null){
            logger.info("servingInformation is exist.");
        }else{
            logger.error(String.format("servingInformation does not exist，servingId is [%s]", servingId.toString()));
            return Result.result(ResultCode.DATA_NOT_EXIST_IN_MYSQL);
        }

        String servingName = servingInformation.getServingName();
        boolean flag = k8sService.deleteDeploy(servingName);
        if(flag){
            logger.info("deploy delete successfully");
        }else{
            logger.error("deploy delete failed");
            return Result.result(ResultCode.DELETE_K8S_DEPLOY_FAILED);
        }
        flag = k8sService.deleteSvc(servingName);
        if(flag){
            logger.info("svc delete successfully");
        }else{
            logger.error("svc delete failed");
            return Result.result(ResultCode.DELETE_K8S_SVC_FAILED);
        }

        String localPath = servingInformation.getModelLocalPath();
        String localPathParent = new File(localPath).getParent();
        try {
            FileUtils.deleteDirectory(new File(localPathParent));
            logger.info("delete local model successfully");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("delete local model failed");
            return Result.result(ResultCode.DELETE_FILE_FAILED);
        }
        Integer delCount = servingMapper.deleteServingInformationByServingId(servingId);
        if(delCount > 0){
            logger.info(String.format("Delete ServingInformation id:[%s] successfully", servingId.toString()));
        }else{
            logger.error(String.format("Delete ServingInformation id:[%s] failed", servingId.toString()));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        return Result.success();
    }

    public Result getServingPage(String name, Integer offset, Integer limit){
        PageHelper.startPage(offset, limit, true);
        List<ServingInformation> servingInformationList = servingMapper.selectServingInformationByServingNamePage(name);
        PageInfo<ServingInformation> servingInformationPageInfo = new PageInfo<>(servingInformationList);
        JSONObject rs = new JSONObject();
        rs.put("list", servingInformationList);
        rs.put("total", servingInformationPageInfo.getTotal());
        rs.put("offset", offset);
        rs.put("limit", limit);
        return Result.success(rs);
    }
}
