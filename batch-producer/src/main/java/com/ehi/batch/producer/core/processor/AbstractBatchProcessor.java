package com.ehi.batch.producer.core.processor;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.listener.BatchJobListener;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.core.job.JobReport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author portz
 * @date 05/16/2022 20:52
 */
@Slf4j
public abstract class AbstractBatchProcessor<I, O> implements Processor, DisposableBean {
    public static final String KEY_PREFIX = "producer-";
    @Autowired
    BatchJobListener batchJobListener;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private List<JobContext> jobCtxList = Lists.newCopyOnWriteArrayList();

    @Override
    public JobReport processJob(JobContext ctx) throws BatchJobException {
        jobCtxList.add(ctx);
        JobBuilder<I, O> jobBuilder = configJobBuilder(ctx);
        Job job = jobBuilder.build();
        JobExecutor jobExecutor = new JobExecutor(1);
        JobReport report = jobExecutor.execute(job);
        jobExecutor.shutdown();
        jobCtxList.remove(ctx);
        // Print the job execution report
        log.info(report.toString());
        return report;
    }

    abstract public JobConfiguration<I, O> config(JobContext ctx) throws BatchJobException;

    /**
     * configure listener, reader, writer etc.
     *
     * @param ctx
     * @return
     */
    private JobBuilder<I, O> configJobBuilder(JobContext ctx) {
        String jobName = String.format("Batch-[%s]-[%s]", ctx.getActionId(), ctx.getRequestToken());
        batchJobListener.setJobCtx(ctx);
        JobBuilder<I, O> jobBuilder = new JobBuilder<I, O>()
                .named(jobName)
                .batchSize(50);
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
            log.warn("Please make sure you've implemented the logic in {} !!!", BatchJobListener.class.getName());
            jobBuilder.jobListener(jobConfiguration.getJobListener());
        } else {
            jobBuilder.jobListener(batchJobListener);
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

    @Override
    public void destroy() throws Exception {
        Gson gson = new GsonBuilder().create();
        for (JobContext ctx : jobCtxList) {
            String key = BatchJobListener.KEY_PREFIX + ctx.getRequestToken();
            redisTemplate.opsForValue().set(key, gson.toJson(ctx));
        }
    }
}
