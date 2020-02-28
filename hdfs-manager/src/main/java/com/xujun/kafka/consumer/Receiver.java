package com.xujun.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.xujun.config.CommonEnvConfig;
import com.xujun.kafka.beans.Message;
import com.xujun.kafka.provider.BuildModelSender;
import com.xujun.kafka.provider.DataProcessSender;
import com.xujun.model.DataInformation;
import com.xujun.model.JobInformation;
import com.xujun.service.HdfsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class Receiver {

    @Autowired
    HdfsService hdfsService;

    @Autowired
    DataProcessSender dataProcessSender;

    @Autowired
    BuildModelSender buildModelSender;


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
                if (!dataProcessSender.send(message1)) {
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
                if (!buildModelSender.send(message1)) {
                    logger.info("kafka send failed");
                }
            }

        }

    }
}