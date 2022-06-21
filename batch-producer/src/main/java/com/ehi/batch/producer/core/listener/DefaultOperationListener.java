package com.ehi.batch.producer.core.listener;

import com.ehi.batch.ExecuteMode;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.ApplicationContextProvider;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.listener.BatchJobEventListener;
import com.google.common.eventbus.EventBus;
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
        String bean = ctx.getExecuteMode() == ExecuteMode.ASYNC ? "asyncEventBus" : "eventBus";
        EventBus eventBus = ApplicationContextProvider.getApplicationContext().getBean(bean, EventBus.class);
        BatchJobEventListener listener = ApplicationContextProvider.getApplicationContext().getBean(BatchJobEventListener.class);
        eventBus.register(listener);
        eventBus.post(ctx);
        eventBus.unregister(listener);
    }
}
