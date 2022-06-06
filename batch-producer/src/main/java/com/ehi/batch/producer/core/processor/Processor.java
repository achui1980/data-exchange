package com.ehi.batch.producer.core.processor;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.context.JobContext;
import org.jeasy.batch.core.job.JobReport;

/**
 * @author portz
 * @date 05/09/2022 9:29
 */
public interface Processor {

    JobReport processJob(JobContext ctx) throws BatchJobException;
}
