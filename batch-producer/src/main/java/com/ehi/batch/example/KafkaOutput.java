package com.ehi.batch.example;

import com.ehi.batch.kafka.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author portz
 * @date 05/09/2022 9:58
 */
@Component("KafkaWriter")
@Slf4j
public class KafkaOutput implements RecordWriter<String> {

    @Autowired
    KafkaSender sender;

    private String topic = "port.test";

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public void writeRecords(Batch<String> batch) throws Exception {
        for (Record<String> msg : batch) {
            //log.info("Writing the data to Kafka \n" + msg);
            sender.send(topic, null, msg.getPayload(), null);
        }
    }
}
