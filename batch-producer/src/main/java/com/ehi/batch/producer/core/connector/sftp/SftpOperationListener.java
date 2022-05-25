package com.ehi.batch.producer.core.connector.sftp;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.context.JobContext;

/**
 * @author portz
 * @date 05/10/2022 16:08
 */
public interface SftpOperationListener {
    void beforeSftpOperation(JobContext ctx) throws BatchJobException;

    void afterSftpOperation(JobContext ctx) throws BatchJobException;
}
