package com.ehi.batch.core.processor;

import com.ehi.batch.core.JobConfiguration;
import com.ehi.batch.core.context.JobContext;
import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.core.job.JobReport;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.writer.RecordWriter;

/**
 * @author portz
 * @date 05/16/2022 20:52
 */
public abstract class AbstractBatchProcessor<I, O> implements Processor {
    @Override
    public JobReport processJob(JobContext ctx) {

        JobBuilder<I,O> jobBuilder = configJobBuilder(ctx);
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
     * @param ctx
     * @return
     */
    private JobBuilder<I, O> configJobBuilder(JobContext ctx) {
        String jobName = String.format("Batch-[%s]-[%s]", ctx.getActionId(), ctx.getRequestToken());
        JobBuilder<I, O> jobBuilder = new JobBuilder<I, O>()
                .named(jobName)
                .batchSize(1);
        JobConfiguration<I, O> jobConfiguration = this.config(ctx);
        if (jobConfiguration.getRecordFilter() != null) {
            jobBuilder.filter(jobConfiguration.getRecordFilter());
        }
        if (jobConfiguration.getBatchSize() > 0) {
            jobBuilder.batchSize(jobConfiguration.getBatchSize());
        }
        if (jobConfiguration.getBatchListener() != null) {
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
