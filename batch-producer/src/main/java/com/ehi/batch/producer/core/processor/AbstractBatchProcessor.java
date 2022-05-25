package com.ehi.batch.producer.core.processor;

import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.listener.BatchJobListener;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.core.job.JobReport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author portz
 * @date 05/16/2022 20:52
 */
@Slf4j
public abstract class AbstractBatchProcessor<I, O> implements Processor {
    @Autowired
    BatchJobListener batchJobListener;

    @Override
    public JobReport processJob(JobContext ctx) {

        JobBuilder<I, O> jobBuilder = configJobBuilder(ctx);
        Job job = jobBuilder.build();
        JobExecutor jobExecutor = new JobExecutor(1);
        JobReport report = jobExecutor.execute(job);
        jobExecutor.shutdown();
        // Print the job execution report
        System.out.println(report);
        return report;
    }

    abstract public JobConfiguration<I, O> config(JobContext ctx);

    /**
     * configure listener, reader, wirtter etc.
     *
     * @param ctx
     * @return
     */
    private JobBuilder<I, O> configJobBuilder(JobContext ctx) {
        String jobName = String.format("Batch-[%s]-[%s]", ctx.getActionId(), ctx.getRequestToken());
        batchJobListener.setJobCtx(ctx);
        JobBuilder<I, O> jobBuilder = new JobBuilder<I, O>()
                .named(jobName)
                .jobListener(batchJobListener)
                .batchSize(1);
        JobConfiguration<I, O> jobConfiguration = this.config(ctx);
        if (jobConfiguration.getRecordFilter() != null) {
            jobBuilder.filter(jobConfiguration.getRecordFilter());
        }
        if (jobConfiguration.getBatchSize() > 0) {
            jobBuilder.batchSize(jobConfiguration.getBatchSize());
        }
        if (jobConfiguration.getBatchListener() != null) {
            log.warn("Please make sure you implement the logic in {} !!!", BatchJobListener.class.getName());
            jobBuilder.batchListener(jobConfiguration.getBatchListener());
        }
        if (jobConfiguration.getJobListener() != null) {
            jobBuilder.jobListener(jobConfiguration.getJobListener());
        }
        if (jobConfiguration.getRecordProcessor() != null) {
            jobBuilder.processor(jobConfiguration.getRecordProcessor());
        }
        if (jobConfiguration.getPipelineListener() != null) {
            jobBuilder.pipelineListener(jobConfiguration.getPipelineListener());
        }
        if (jobConfiguration.getRecordMapper() != null) {
            jobBuilder.mapper(jobConfiguration.getRecordMapper());
        }
        if (jobConfiguration.getRecordMarshaller() != null) {
            jobBuilder.marshaller(jobConfiguration.getRecordMarshaller());
        }
        if (jobConfiguration.getRecordValidator() != null) {
            jobBuilder.validator(jobConfiguration.getRecordValidator());
        }
        if (jobConfiguration.getRecordWriter() != null) {
            jobBuilder.writer(jobConfiguration.getRecordWriter());
        }
        if (jobConfiguration.getRecordWriterListener() != null) {
            jobBuilder.writerListener(jobConfiguration.getRecordWriterListener());
        }
        if (jobConfiguration.getRecordReader() != null) {
            jobBuilder.reader(jobConfiguration.getRecordReader());
        }
        if (jobConfiguration.getRecordReaderListener() != null) {
            jobBuilder.readerListener(jobConfiguration.getRecordReaderListener());
        }
        return jobBuilder;
    }
}
