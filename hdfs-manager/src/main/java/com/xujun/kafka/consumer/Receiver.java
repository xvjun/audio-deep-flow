package com.xujun.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.xujun.config.CommonEnvConfig;
import com.xujun.kafka.beans.Message;
import com.xujun.model.DataInformation;
import com.xujun.model.JobInformation;
import com.xujun.model.ServingInformation;
import com.xujun.service.HdfsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class Receiver {

    @Autowired
    HdfsService hdfsService;

    @Value("${spring.kafka.hdfs2model-topics}")
    private String hdfs2modelTopics;

    @Value("${spring.kafka.hdfs2data-topics}")
    private String hdfs2dataTopics;

    @Value("${spring.kafka.hdfs2serving-topics}")
    private String hdfs2servingTopics;

    @Autowired
    com.xujun.kafka.provider.Sender Sender;


    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    @KafkaListener(topics = "${spring.kafka.receiver-topics}")
    public void listen(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();
            logger.info("record = {}", record);
            logger.info("message = {}", message);
            Message message1 = JSONObject.parseObject(message.toString(), Message.class);

            if (message1.getModel().equals(CommonEnvConfig.DATA_PROCESS_HDFS_HDFS_KAFKA_MODEL)){
                DataInformation information = null;
                DataInformation dataInformation = JSONObject.parseObject(message1.getObject().toString(), DataInformation.class);
                Map<String, String> map = message1.getDataMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    information = hdfsService.dataProcessHdfsToHdfs(entry.getKey(), entry.getValue(), dataInformation);
                }
                message1.setObject(information);
                if (!Sender.send(hdfs2dataTopics, message1)) {
                    logger.info("kafka send failed");
                }
            }else if(message1.getModel().equals(CommonEnvConfig.BUILD_MODEL_HDFS_LOCAL_KAFKA_MODEL)){
                JobInformation jobInformation = JSONObject.parseObject(message1.getObject().toString(), JobInformation.class);
                Map<String, String> map = message1.getDataMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if(jobInformation.getIsCompleted().equals(CommonEnvConfig.IS_COMPLETED_FAILED)){
                        break;
                    }else{
                        jobInformation = hdfsService.buildModelHdfsToLocal(entry.getKey(), entry.getValue(), jobInformation);
                    }
                }
                message1.setObject(jobInformation);
                if (!Sender.send(hdfs2modelTopics, message1)) {
                    logger.info("kafka send failed");
                }
            }else if(message1.getModel().equals(CommonEnvConfig.SERVING_APP_HDFS_LOCAL_KAFKA_MODEL)){
                ServingInformation servingInformation = JSONObject.parseObject(message1.getObject().toString(), ServingInformation.class);
                Map<String, String> map = message1.getDataMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if(servingInformation.getIsCompleted().equals(CommonEnvConfig.IS_COMPLETED_FAILED)){
                        break;
                    }else{
                        servingInformation = hdfsService.servingAppHdfsToLocal(entry.getKey(), entry.getValue(), servingInformation);
                    }
                }
                message1.setObject(servingInformation);
                if (!Sender.send(hdfs2servingTopics, message1)) {
                    logger.info("kafka send failed");
                }
            }

        }

    }
}