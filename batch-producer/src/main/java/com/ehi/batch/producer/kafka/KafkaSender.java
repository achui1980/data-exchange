package com.ehi.batch.producer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author portz
 * @date 04/24/2022 20:57
 */
@Component
@Slf4j
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //发送消息方法
    public void send(String msg) {
        this.send("port.test", null, msg, null);
    }

    //发送消息方法
    public void send(String topic, String key, String msg, List<Map<String, String>> headers) {
        ProducerRecord<String, String> record = new ProducerRecord(topic, key, msg);
        if (CollectionUtils.isNotEmpty(headers)) {
            for (Map<String, String> header : headers) {
                for (Map.Entry<String, String> head : header.entrySet()) {
                    record.headers().add(head.getKey(), head.getValue().getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        kafkaTemplate.send(record);
    }
}
