package com.xujun.service;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.xujun.config.CommonEnvConfig;
import com.xujun.config.StreamEnvConfig;
import com.xujun.dao.StreamMapper;
import com.xujun.model.StreamInformation;
import com.xujun.model.req.CreateStreamRequest;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StreamService {

    private static Logger logger = LoggerFactory.getLogger(StreamService.class);

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);

    @Autowired
    StreamMapper streamMapper;

    @Value("${stream.predictor.front.service.image}")
    private String image;

    @Value("${audio-deep-flow.k8s.namespace}")
    private String k8sNamespace;

    @Value("${mysql.address}")
    private String mysqlAddress;

    @Autowired
    private K8sService k8sService;

    /**
     *  启动deploy，写mysql
     * @param request
     * @return
     */
    public Result createStream(CreateStreamRequest request) {

        long ts = System.currentTimeMillis();
        String startTime = df.format(new Date(ts));

        if(request.getReceiverTopics() == null){
            request.setReceiverTopics(StreamEnvConfig.DEFAULT_RECEIVER_TOPIC);
        }
        if(request.getSendTopics() == null){
            request.setSendTopics(StreamEnvConfig.DEFAULT_SEND_TOPIC);
        }
        StreamInformation streamInformation = new StreamInformation();
        streamInformation.setStreamId(0);
        streamInformation.setStreamName("stream-"+ts);
        streamInformation.setStartTime(startTime);
        streamInformation.setServingName(request.getServingName());
        streamInformation.setCpu(request.getCpu());
        streamInformation.setMemory(request.getMemory());
        streamInformation.setInstance(request.getInstance());
        streamInformation.setKafkaAddress(request.getKafkaAddress());
        streamInformation.setReceiverTopics(request.getReceiverTopics());
        streamInformation.setSendTopics(request.getSendTopics());
        streamInformation.setNodePort(request.getNodePort());

        // k8s启动svc,deploy

        String label = streamInformation.getStreamName();
        String svcName = streamInformation.getStreamName();

        String deployName = streamInformation.getStreamName();
        Integer replicas = streamInformation.getInstance();
        Float cpu = streamInformation.getCpu();
        Integer memory = streamInformation.getMemory();
        String kafkaAddress = streamInformation.getKafkaAddress();
        String receiverTopics = streamInformation.getReceiverTopics();
        String sendTopics = streamInformation.getSendTopics();
        String url = String.format("http://%s:8501/v1/models/model:predict", streamInformation.getServingName());
        Integer nodePort = streamInformation.getNodePort();

        Map<String, Object> params = new HashMap<>();

        params.put("label", label);
        params.put("svcName", svcName);
        params.put("k8sNamespace", k8sNamespace);
        params.put("deployName", deployName);
        params.put("replicas", replicas);
        params.put("cpu", cpu);
        params.put("memory", memory);
        params.put("image", image);
        params.put("kafkaAddress", kafkaAddress);
        params.put("receiverTopics", receiverTopics);
        params.put("sendTopics", sendTopics);
        params.put("url", url);
        params.put("mysqlAddress", mysqlAddress);
        params.put("nodePort", nodePort);

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
        System.out.println(svcRenderedTemplate);
        System.out.println(deployRenderedTemplate);

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
        int addCount = streamMapper.addStreamInfomation(streamInformation);
        if(addCount > 0){
            logger.info("Mysql streamInformation update successfully");
        }else{
            logger.error(String.format("Mysql streamInformation update failed, streamInformation is [%s]", streamInformation));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }

        return Result.success();
    }


//    删除deploy，mysql
    public Result deleteStreamById(Integer streamId) {
        StreamInformation streamInformation = streamMapper.selectStreamInformationByStreamId(streamId);
        if(streamInformation != null){
            logger.info("streamInformation is exist.");
        }else{
            logger.error(String.format("streamInformation does not exist，streamId is [%s]", streamId.toString()));
            return Result.result(ResultCode.DATA_NOT_EXIST_IN_MYSQL);
        }

        String streamName = streamInformation.getStreamName();
        boolean flag = k8sService.deleteDeploy(streamName);
        if(flag){
            logger.info("deploy delete successfully");
        }else{
            logger.error("deploy delete failed");
            return Result.result(ResultCode.DELETE_K8S_DEPLOY_FAILED);
        }
        flag = k8sService.deleteSvc(streamName);
        if(flag){
            logger.info("svc delete successfully");
        }else{
            logger.error("svc delete failed");
            return Result.result(ResultCode.DELETE_K8S_SVC_FAILED);
        }

        Integer delCount = streamMapper.deleteStreamInformationByStreamId(streamId);
        if(delCount > 0){
            logger.info(String.format("Delete streamInformation id:[%s] successfully", streamId.toString()));
        }else{
            logger.error(String.format("Delete streamInformation id:[%s] failed", streamId.toString()));
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }
        return Result.success();
    }

    // 查询mysql
    public Result getStreamPage(String displayName, Integer offset, Integer limit) {
        PageHelper.startPage(offset, limit, true);
        List<StreamInformation> streamInformationList = streamMapper.selectStreamInformationByStreamNamePage(displayName);
        PageInfo<StreamInformation> streamInformationPageInfo = new PageInfo<>(streamInformationList);
        JSONObject rs = new JSONObject();
        rs.put("list", streamInformationList);
        rs.put("total", streamInformationPageInfo.getTotal());
        rs.put("offset", offset);
        rs.put("limit", limit);
        return Result.success(rs);
    }
}
