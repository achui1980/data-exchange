package com.ehi.batch.producer.core.connector;

import com.ehi.batch.producer.core.context.JobContext;

/**
 * @author portz
 * @date 05/10/2022 14:20
 */
public interface Connector {
    void download(JobContext ctx);
}
