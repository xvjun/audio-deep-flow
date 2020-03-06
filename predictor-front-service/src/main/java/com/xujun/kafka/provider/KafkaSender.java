package com.xujun.kafka.provider;

import com.alibaba.fastjson.JSONObject;
import com.xujun.model.PredictorInformation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class KafkaSender {

    private static Logger logger = LoggerFactory.getLogger(KafkaSender.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.send-topics}")
    private String topics;

    //发送消息方法
    public boolean send(PredictorInformation predictorInformation) {
        String messageJson = JSONObject.toJSONString(predictorInformation);
        logger.info("message = {}", messageJson);
        try{
            kafkaTemplate.send(topics, messageJson);
            logger.info("kafka send successfully");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            logger.error("kafka send failed");
            return false;
        }
    }
}
