package com.ehi.batch.kafka;

import com.ehi.batch.consumer.ConsumerJobContext;
import com.ehi.batch.consumer.DefaultRecorderHandler;
import com.ehi.batch.domain.MessageHeader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author portz
 * @date 04/24/2022 20:59
 */
@Component
@Slf4j
public class KafkaReceiver {

    @Autowired
    DefaultRecorderHandler processor;

    ObjectMapper mapper;

    @PostConstruct
    public void register() {
        mapper = new ObjectMapper();
    }

    @KafkaListener(topics = {"port.test"})
    public void listen(ConsumerRecord<?, ?> record, @Header("X-Batch-Meta-Json") String metaJson) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                MessageHeader messageMeta = mapper.readValue(metaJson, MessageHeader.class);
                ConsumerJobContext ctx = ConsumerJobContext.builder()
                        .messageMeta(messageMeta)
                        .message(message)
                        .build();
                processor.processRecord(ctx);

            } catch (Exception e) {
                log.error("error while covert to class", e);
            }
        }

    }
}