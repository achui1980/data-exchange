package com.ehi.batch.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author portz
 * @date 04/24/2022 20:59
 */
@Component
@Slf4j
public class KafkaReceiver {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @KafkaListener(topics = {"port.test"})
    public void listen(ConsumerRecord<?, ?> record, @Headers MessageHeaders headers) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("----------------- record =" + record);
            log.info("------------------ message =" + message);
            //ETL Process
            redisTemplate.opsForSet().add("actionId", message.toString());
        }

    }
}