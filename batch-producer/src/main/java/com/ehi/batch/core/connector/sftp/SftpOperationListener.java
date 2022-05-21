package com.ehi.batch.core.connector.sftp;

import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.exception.BatchJobException;

/**
 * @author portz
 * @date 05/10/2022 16:08
 */
public interface SftpOperationListener {
    void beforeSftpOperation(JobContext ctx) throws BatchJobException;

    void afterSftpOperation(JobContext ctx) throws BatchJobException;
}
