package com.ehi.batch.example;

import com.ehi.batch.kafka.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author portz
 * @date 05/09/2022 9:58
 */
@Component ("KafkaWriter")
@Slf4j
public class KafkaOutput implements ItemWriter<String> {

    @Autowired
    KafkaSender sender;

    private String topic = "port.test";

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public void write(List<? extends String> messages) throws Exception {
        for (String msg : messages) {
            log.info("Writing the data to Kafka \n" + msg);
            sender.send(topic, msg, null);
        }
    }
}
