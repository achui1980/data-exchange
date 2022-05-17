package com.ehi.batch.core.processor;

import com.ehi.batch.core.context.JobContext;
import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.core.job.JobReport;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.record.Header;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;

/**
 * @author portz
 * @date 05/16/2022 20:52
 */
public abstract class DefaultBatchProcessor<I, O> implements Processor {
    @Override
    public Job processJob(JobContext ctx) {
        String jobName = String.format("Batch-[%s]-[%s]", ctx.getActionId(), ctx.getRequestToken());
        Job job = new JobBuilder<I, O>()
                .named(jobName)
                .reader(this.getReaderBean(ctx))
                .writer(this.getWriterBean(ctx))
                .processor(this.getItemProcessBean(ctx))
                .build();
        JobExecutor jobExecutor = new JobExecutor(1);
        JobReport report = jobExecutor.execute(job);
        jobExecutor.shutdown();

        // Print the job execution report
        System.out.println(report);
        return null;
    }

    /**
     * the item reader instance that handler read logic
     *
     * @return
     */
    abstract public RecordReader<I> getReaderBean(JobContext ctx);

    /**
     * the item writer instance that handler written logic
     *
     * @return
     */
    abstract public RecordWriter<O> getWriterBean(JobContext ctx);

    /**
     * the process instance that handler ETL logic
     *
     * @return
     */
    public RecordProcessor<I, O> getItemProcessBean(JobContext ctx) {
        return new RecordProcessor<I, O>() {
            @Override
            public Record<O> processRecord(Record<I> record) throws Exception {
                return new Record<O>() {
                    @Override
                    public Header getHeader() {
                        return null;
                    }

                    @Override
                    public O getPayload() {
                        return (O) record.getPayload();
                    }
                };
            }
        };
    }
}
