package com.ehi.batch.producer.listener;

import com.ehi.batch.PropertyConstant;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.processor.Processor;
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
        String processBean = jobCtx.getActionProps().getStr(PropertyConstant.BATCH_JOB_PROCESSOR_NAME);
        Processor processor = appCtx.getBean(processBean, Processor.class);
        processor.processJob(jobCtx);
        log.info("====== trigger job EventBus end ======");
    }
}
