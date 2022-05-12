package com.ehi.batch.core.processor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author portz
 * @date 05/09/2022 11:18
 */
public abstract class AbstractBatchProcessor<I, O> implements Processor {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Override
    public Job processJob() {
        FlowJobBuilder jobBuilder = jobBuilderFactory.get("batch-" + RandomStringUtils.randomAlphabetic(6))
                .incrementer(new RunIdIncrementer())
                .flow(step())
                .end();
        for (JobExecutionListener listener : listeners()) {
            jobBuilder.listener(listener);
        }
        return jobBuilder.build();
    }

    public Step step() {
        TaskletStep tasklet = null;
        SimpleStepBuilder stepBuilder = stepBuilderFactory
                .get("step1")
                .<I, O>chunk(getChunkSize())
                .reader(getReaderBean())
                .processor(getItemProcessBean())
                .writer(getWriterBean());
        if (getRetryTimes() > 0 && CollectionUtils.isNotEmpty(retryExceptionList())) {
            FaultTolerantStepBuilder faultTolerant = stepBuilder
                    .faultTolerant()
                    .retryLimit(getRetryTimes());
            for (Class exception : retryExceptionList()) {
                faultTolerant.retry(exception);
            }
            tasklet = faultTolerant.build();
        } else {
            tasklet = stepBuilder.build();
        }
        return tasklet;
    }

    /**
     * add list listeners when Job execute.
     * @return
     */
    abstract public List<JobExecutionListener> listeners();

    /**
     * the item reader instance that handler read logic
     * @return
     */
    abstract public ItemReader<I> getReaderBean();

    /**
     * the item writer instance that handler written logic
     * @return
     */
    abstract public ItemWriter<O> getWriterBean();

    /**
     * the process instance that handler ETL logic
     * @return
     */
    abstract public ItemProcessor<I, O> getItemProcessBean();

    /**
     * exception list that need to retry.
     * this method can be overwritten by subclass.
     * @return
     */
     public List<Class<? extends Throwable>> retryExceptionList() {
        return null;
     }

    /**
     * How many record as a batch process by spring batch, default is 1
     * this method can be overwritten by subclass.
     * @return int
     */
    public int getChunkSize() {
        return 1;
    }

    /**
     * How many times need to retry, default is 3,
     * only retryExceptionList() return none empty list, this parameter will take effect
     * this method can be overwritten by subclass.
     * @return int
     */
    public int getRetryTimes() {
        return 3;
    }
}
