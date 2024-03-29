package com.xujun.kafka.provider;

import com.alibaba.fastjson.JSONObject;
import com.xujun.kafka.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class Sender {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    //发送消息方法
    public boolean send(String topics, Message message) {
        String messageJson = JSONObject.toJSONString(message);
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