package com.ehi.batch.producer.listener;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.context.FetchContext;
import com.ehi.batch.producer.core.context.JobContext;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 05/16/2022 16:19
 */
@Component
@Slf4j
public class FetchFileEventListener {
    @Autowired
    private ApplicationContext appCtx;

    @Subscribe
    public void triggerBatchJob(FetchContext fetchCtx) throws BatchJobException {
        log.info("========= begin to download ===========");
        JobContext jobCtx = JobContext.builder().build();
        jobCtx.convertFrom(fetchCtx);
        String connectorBeanName = fetchCtx.getActionProps().getStr("batch.job.connector", "SftpConnector");
        Connector connector = appCtx.getBean(connectorBeanName, Connector.class);
        connector.download(jobCtx);
        log.info("========= end download ===========");
    }
}
