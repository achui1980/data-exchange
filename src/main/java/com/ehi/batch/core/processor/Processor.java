package com.ehi.batch.core.processor;

import org.springframework.batch.core.Job;

/**
 * @author portz
 * @date 05/09/2022 9:29
 */
public interface Processor {

    Job processJob();
}
