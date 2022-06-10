package com.ehi.batch.producer.core.listener;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.ApplicationContextProvider;
import com.ehi.batch.producer.core.listener.OperationListener;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.listener.BatchJobEventListener;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author portz
 * @date 05/10/2022 16:19
 */
@Slf4j
public class DefaultOperationListener implements OperationListener {
    @Override
    public void beforeOperation(JobContext ctx) {

    }

    @Override
    public void afterOperation(JobContext ctx) throws BatchJobException {
        AsyncEventBus asyncEventBus = ApplicationContextProvider.getApplicationContext().getBean(AsyncEventBus.class);
        BatchJobEventListener listener = ApplicationContextProvider.getApplicationContext().getBean(BatchJobEventListener.class);
        asyncEventBus.register(listener);
        asyncEventBus.post(ctx);
        asyncEventBus.unregister(listener);
    }
}
