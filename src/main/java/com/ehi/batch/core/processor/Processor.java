package com.ehi.batch.core.processor;

import com.ehi.batch.core.context.JobContext;
import org.jeasy.batch.core.job.Job;

/**
 * @author portz
 * @date 05/09/2022 9:29
 */
public interface Processor {

    Job processJob(JobContext ctx);
}
