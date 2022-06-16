package com.ehi.batch.producer.core.listener;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.ApplicationContextProvider;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 06/15/2022 17:24
 */
@Component
public class KafkaSendFlagListener implements OperationListener{
    @Autowired
    private KafkaSender kafkaSender;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public void beforeOperation(JobContext ctx) throws BatchJobException {
        kafkaSender.sendKafkaJobFlag(topic, "Batch Start", ctx, true, false);
    }

    @Override
    public void afterOperation(JobContext ctx) throws BatchJobException {
        kafkaSender.sendKafkaJobFlag(topic, "Batch Complete", ctx, false, true);
    }
}
