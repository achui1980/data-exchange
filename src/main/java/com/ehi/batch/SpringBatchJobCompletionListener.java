package com.ehi.batch;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 * @author portz
 * @date 04/24/2022 16:22
 */
@Slf4j
public class SpringBatchJobCompletionListener extends JobExecutionListenerSupport {
    private long startTime;
    private long endTime;
    @Override
    public void beforeJob(JobExecution jobExecution) {
        super.beforeJob(jobExecution);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        endTime = System.currentTimeMillis();
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("BATCH JOB COMPLETED SUCCESSFULLY");
        }
    }

}
