package com.ehi.batch.producer.listener;

import com.ehi.batch.JobStatus;
import com.ehi.batch.PropertyConstant;
import com.ehi.batch.model.MessageHeader;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.job.JobParameters;
import org.jeasy.batch.core.job.JobReport;
import org.jeasy.batch.core.listener.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author portz
 * @date 05/18/2022 21:34
 */
@Component
@Slf4j
public class BatchJobListener implements JobListener {
    @Autowired
    KafkaSender sender;

    @Setter
    JobContext jobCtx;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public void beforeJob(JobParameters jobParameters) {
        sender.sendKafkaJobFlag(topic, "Batch Start", jobCtx, JobStatus.START);
    }

    @Override
    public void afterJob(JobReport jobReport) {
        sender.sendKafkaJobFlag(topic, "Batch Complete", jobCtx, JobStatus.COMPLETED);
    }
}
