package com.ehi.batch.producer.core.listener;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.context.JobContext;

/**
 * @author portz
 * @date 05/10/2022 16:08
 */
public interface OperationListener {
    void beforeOperation(JobContext ctx) throws BatchJobException;

    void afterOperation(JobContext ctx) throws BatchJobException;
}
