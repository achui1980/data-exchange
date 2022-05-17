package com.ehi.batch.core.connector.sftp;

import com.ehi.batch.core.ApplicationContextProvider;
import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.exception.BatchJobException;
import com.ehi.batch.listener.BatchJobEventListener;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author portz
 * @date 05/10/2022 16:19
 */
@Slf4j
public class DefaultSftpOperationListener implements SftpOperationListener {
    @Override
    public void beforeSftpOperation(JobContext ctx) {

    }

    @Override
    public void afterSftpOperation(JobContext ctx) throws BatchJobException {
        AsyncEventBus asyncEventBus = ApplicationContextProvider.getApplicationContext().getBean(AsyncEventBus.class);
        BatchJobEventListener listener = ApplicationContextProvider.getApplicationContext().getBean(BatchJobEventListener.class);
        asyncEventBus.register(listener);
        asyncEventBus.post(ctx);
    }
}
