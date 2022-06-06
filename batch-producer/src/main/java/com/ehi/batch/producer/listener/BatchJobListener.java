package com.ehi.batch.producer.listener;

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

    @Override
    public void beforeJob(JobParameters jobParameters) {
        List<Map<String, String>> headers = Lists.newArrayList();
        MessageHeader messageHeader = MessageHeader.builder()
                .actionId(jobCtx.getActionId())
                .objectModel(jobCtx.getActionProps().getStr("batch.record.mapper.class"))
                .requestToken(jobCtx.getRequestToken())
                .jobComplete(false)
                .jobStart(true)
                .build();
        Map<String, String> header = Maps.newHashMap();
        header.put("X-Batch-Meta-Json", messageHeader.toString());
        headers.add(header);
        sender.send("port.test", jobCtx.getActionId(), "Batch Start", headers);
    }

    @Override
    public void afterJob(JobReport jobReport) {
        List<Map<String, String>> headers = Lists.newArrayList();
        MessageHeader messageHeader = MessageHeader.builder()
                .actionId(jobCtx.getActionId())
                .objectModel(jobCtx.getActionProps().getStr("batch.record.mapper.class"))
                .requestToken(jobCtx.getRequestToken())
                .jobComplete(true)
                .jobStart(false)
                .build();
        Map<String, String> header = Maps.newHashMap();
        header.put("X-Batch-Meta-Json", messageHeader.toString());
        headers.add(header);
        sender.send("port.test", jobCtx.getActionId(), "Batch complete", headers);
    }
}
