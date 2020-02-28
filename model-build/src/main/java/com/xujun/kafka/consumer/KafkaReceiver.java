package com.xujun.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.xujun.kafka.beans.Message;
import com.xujun.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiver {

    private static Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Autowired
    ModelService modelService;

    @KafkaListener(topics = "${spring.kafka.receiver-topics}")
    public void listen(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();

            logger.info("record = {}", record);
            logger.info("message = {}", message);
            Message messageInfo = JSONObject.parseObject(message.toString(), Message.class);
            modelService.kafkaJobCallBack(messageInfo);

        }

    }
}
