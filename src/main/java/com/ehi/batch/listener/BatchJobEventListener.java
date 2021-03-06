package com.ehi.batch.listener;

import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.exception.BatchJobException;
import com.ehi.batch.core.processor.Processor;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 05/16/2022 13:54
 */
@Component
@Slf4j
public class BatchJobEventListener {

    @Autowired
    private ApplicationContext appCtx;

    @Subscribe
    public void triggerBatchJob(JobContext jobCtx) throws BatchJobException {
        log.info("====== trigger job from EventBus begin  ======");
        String processBean = jobCtx.getActionProps().getStr("batch.job.processor.name");
        Processor processor = appCtx.getBean(processBean, Processor.class);
        processor.processJob(jobCtx);
        log.info("====== trigger job EventBus end ======");
    }
}
