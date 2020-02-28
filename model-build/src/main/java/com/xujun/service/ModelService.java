package com.xujun.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.xujun.config.CommonEnvConfig;
import com.xujun.dao.JobMapper;
import com.xujun.kafka.beans.Message;
import com.xujun.kafka.provider.KafkaSender;
import com.xujun.model.JobInformation;
import com.xujun.model.req.CreateModelRequest;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.utils.HdfsUtils;
import com.xujun.utils.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ModelService {

    private static Logger logger = LoggerFactory.getLogger(ModelService.class);

    @Autowired
    KafkaSender kafkaSender;

    @Value("${buildmodel.path}")
    private String localPath;

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);

    @Autowired
    JobMapper jobMapper;

    @Value("${audio-deep-flow.k8s.namespace}")
    private String k8sNamespace;

    @Value("${audio-deep-flow.buildmodel.job.image}")
    private String image;

    @Autowired
    K8sService k8sService;

    /**
     * 1.下载数据到本地的目录中
     * 2.构造conf文件
     * 3.创建输出的映射目录
     *
     * 4.通过k8s启动job
     * 5.写mysql
     *
     *
     * 1.发kafka，写mysql
     * @param request
     * @return
     */

    public Result createModel(CreateModelRequest request){
        String namespace = UuidUtils.createUUID();
        String basePath = Paths.get(localPath, namespace).toString();
        File basePathFile = new File(basePath);
        if (!basePathFile.exists()) {
            basePathFile.mkdirs();
        }
        String localPathDir = Paths.get(localPath, namespace, "data").toString();
        File localPathDirFile = new File(localPathDir);
        if (!localPathDirFile.exists()) {
            localPathDirFile.mkdirs();
        }
        String historyPathDir = Paths.get(localPath, namespace, "save", "history").toString();
        File historyPathFile = new File(historyPathDir);
        if (!historyPathFile.exists()) {
            historyPathFile.mkdirs();
        }
        String tockPathDir = Paths.get(localPath, namespace, "save", "tock").toString();
        File tockPathFile = new File(tockPathDir);
        if (!tockPathFile.exists()) {
            tockPathFile.mkdirs();
        }
        String pyPath = Paths.get(localPath, namespace, "build_model.py").toString();
        File pyPathFile = new File(pyPath);
        if(!pyPathFile.exists()){
            try {
                pyPathFile.createNewFile();
                logger.info("build_model.py create successfully");
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("build_model.py create failed");
                return Result.result(ResultCode.CREATE_FILE_FAILED, e.getMessage());
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        ClassPathResource resource = new ClassPathResource("build_model.py");
        try {
            InputStream inputStream= resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line+"\n");
            }
            String pythonScript = stringBuffer.toString();
            FileWriter fileWritter = new FileWriter(pyPathFile);
            fileWritter.write(pythonScript);
            fileWritter.close();
            logger.info("copy build_model.py successfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("copy build_model.py failed");
            return Result.result(ResultCode.CREATE_FILE_FAILED, e.getMessage());
        }

        if(request.getDataHdfsPathList().size() == 0){
            logger.error("dataList is null");
            return Result.result(ResultCode.DATA_LIST_IS_NULL);
        }

        // conf and output
        String conf = null;
        try {
            conf = Resources.toString(Resources.getResource("model.conf"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Failed to read conf template");
            return Result.result(ResultCode.READ_FILE_FAILED,e.getMessage());
        }
        Map<String, Object> confParams = new HashMap<>();
        if(request.getHiddenLayers() <= 0){
            confParams.put("hidden_layers", 20);
        }else{
            confParams.put("hidden_layers", request.getHiddenLayers());
        }
        if(request.getLayersSize() <= 0){
            confParams.put("layers_size", 100);
        }else{
            confParams.put("layers_size", request.getLayersSize());
        }
        if(request.getLearningRate() <= 0){
            confParams.put("learning_rate", -1);
        }else{
            confParams.put("learning_rate", request.getLearningRate());
        }
        if(request.getEpochs() <= 0){
            confParams.put("epochs", 100);
        }else{
            confParams.put("epochs", request.getEpochs());
        }
        if(request.getDropoutRate() <= 0){
            confParams.put("dropout_rate", 0.5);
        }else{
            confParams.put("dropout_rate", request.getDropoutRate());
        }
        if(request.getClassSum() <= 0){
            confParams.put("class_sum", 4);
        }else{
            confParams.put("class_sum", request.getClassSum());
        }

        Jinjava jinjava = new Jinjava();
        try{
            String confPath = Paths.get(localPath, namespace, "model.conf").toString();
            File confPathFile = new File(confPath);
            if (!confPathFile.exists()) {
                confPathFile.createNewFile();
            }
            String renderedTemplate = jinjava.render(conf, confParams);
            FileWriter fileWritter = new FileWriter(confPathFile);
            fileWritter.write(renderedTemplate);
            fileWritter.close();
            logger.info("Create model.conf successfully");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Create model.conf failed");
            return Result.result(ResultCode.CREATE_MODEL_CONF_FAILED, e.getMessage());
        }


        //job
        long ts = System.currentTimeMillis();
        String startTime = df.format(new Date(ts));

        JobInformation jobInformation = new JobInformation();
        jobInformation.setJobId(0);
        jobInformation.setJobName("job-"+ts);
        jobInformation.setStartTime(startTime);
        jobInformation.setHiddenLayers(Integer.parseInt(confParams.get("hidden_layers").toString()));
        jobInformation.setLayersSize(Integer.parseInt(confParams.get("layers_size").toString()));
        jobInformation.setLearningRate(Float.parseFloat(confParams.get("learning_rate").toString()));
        jobInformation.setEpochs(Integer.parseInt(confParams.get("epochs").toString()));
        jobInformation.setDropoutRate(Float.parseFloat(confParams.get("dropout_rate").toString()));
        jobInformation.setClassSum(Integer.parseInt(confParams.get("class_sum").toString()));
        jobInformation.setDataLength(0l);
        jobInformation.setTime(0l);
        jobInformation.setDataShardSum(request.getDataHdfsPathList().size());
        jobInformation.setDataShardReadySum(0);
        jobInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_UPLOADING);
        jobInformation.setRootPath(basePath);
        jobInformation.setCpu(request.getCpu());
        jobInformation.setMemory(request.getMemory());

        // mysql
        int addCount = jobMapper.addJobInfomation(jobInformation);
        if(addCount > 0){
            logger.info("Mysql jobInformation insert successfully");
        }else{
            logger.error(String.format("Mysql jobInformation insert failed, dataInformation is [%s]", jobInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }


        //kafka
        logger.info(String.format("dataList : %s", request.getDataHdfsPathList().toString()));
        Message message = new Message();
        message.setModel(CommonEnvConfig.BUILD_MODEL_HDFS_LOCAL_KAFKA_MODEL);
        Map<String, String> dataMap = new HashMap<>();
        for (String hdfsPath : request.getDataHdfsPathList()) {
            String localPath = Paths.get(localPathDir, HdfsUtils.getFileNameFromHdfsUrl(hdfsPath)).toString();
            dataMap.put(hdfsPath, localPath);
        }
        message.setDataMap(dataMap);
        message.setObject(jobInformation);
        if (kafkaSender.send(message)) {
            logger.info("kafka send successfully");
        }else{
            logger.error("kafka send failed");
            jobInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            int updateCount = jobMapper.updateJobInfomationByJobId(jobInformation);
            if(updateCount > 0){
                logger.warn("Mysql jobInformation kafka send failed,update successfully");
            }else{
                logger.error(String.format("Mysql jobInformation callback kafka send failed, jobInformation is [%s]", jobInformation));
                return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
            }
            return Result.result(ResultCode.KAFKA_SEND_FAILED);
        }

        return Result.result(ResultCode.WAITING_DATA_UPLOAD);
    }

    /**
     * kafka的回调
     * 1.判断数据下载是否完整，
     * 1.k8s启动job，
     * 2.更新mysql
     * @param message
     * @return
     */
    public Result kafkaJobCallBack(Message message){
        JobInformation jobInformation = JSONObject.parseObject(message.getObject().toString(), JobInformation.class);
        if(jobInformation.getDataShardReadySum() != jobInformation.getDataShardSum()){
            logger.error("data download failed");
            jobInformation.setIsCompleted(CommonEnvConfig.IS_COMPLETED_FAILED);
            int updateCount = jobMapper.updateJobInfomationByJobId(jobInformation);
            if(updateCount > 0){
                logger.warn("Mysql jobInformation data download failed,update successfully");
            }else{
                logger.error(String.format("Mysql jobInformation data download send failed, jobInformation is [%s]", jobInformation));
                return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
            }
        }
        // k8s启动job
        Map<String, Object> jobParams = new HashMap<>();
        jobParams.put("jobName", jobInformation.getJobName());
        jobParams.put("k8sNamespace", k8sNamespace);
        jobParams.put("volume", jobInformation.getRootPath());
        jobParams.put("image", image);
        jobParams.put("cpu", jobInformation.getCpu());
        jobParams.put("memory", jobInformation.getMemory());
        Jinjava jinjava = new Jinjava();
        String jobTemplate = null;
        try {
            jobTemplate = Resources.toString(Resources.getResource("job.yaml.template.json"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Failed to read conf template");
            return Result.result(ResultCode.READ_FILE_FAILED,e.getMessage());
        }
        String renderedTemplate = jinjava.render(jobTemplate, jobParams);
        Optional<Result> ro = k8sService.createJob(renderedTemplate);
        if (ro.isPresent()) {
            logger.error("k8s job start failed");
            return ro.get();
        }else{
            logger.info("k8s job start successfully");
        }


        // mysql
        int updateCount = jobMapper.updateJobInfomationByJobId(jobInformation);
        if(updateCount > 0){
            logger.warn("Mysql jobInformation update failed,update successfully");
        }else{
            logger.error(String.format("Mysql jobInformation update send failed, jobInformation is [%s]", jobInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }


        return Result.success();
    }

    /**
     * 1.对模型压缩，保存到hdfs，写mysql
     * 2.获得history和tock
     * @return
     */

}
