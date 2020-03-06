package com.xujun.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.xujun.config.PredictorEnvConfig;
import com.xujun.model.req.Request;
import com.xujun.service.PredictorFrontService;
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
    PredictorFrontService predictorFrontService;

    @KafkaListener(topics = "${spring.kafka.receiver-topics}")
    public void listen(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();
            logger.info("message = {}", message);
            Request request = null;
            try{
                request = JSONObject.parseObject(message.toString(), Request.class);
            } catch(Exception e){
                e.printStackTrace();
                logger.error("data parse error");
            }
            predictorFrontService.sendData(request, PredictorEnvConfig.FROM_KAFKA);

        }

    }
}
