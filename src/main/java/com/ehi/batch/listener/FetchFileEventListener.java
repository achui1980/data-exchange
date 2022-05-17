package com.ehi.batch.listener;

import com.ehi.batch.SpringBatchJobController;
import com.ehi.batch.core.context.FetchContext;
import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.connector.sftp.SftpTemplate;
import com.ehi.batch.core.exception.BatchJobException;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;

/**
 * @author portz
 * @date 05/16/2022 16:19
 */
@Component
@Slf4j
public class FetchFileEventListener {
    @Autowired
    private AsyncEventBus asyncEventBus;

    @Autowired
    private ApplicationContext appCtx;

    @Subscribe
    public void triggerBatchJob(FetchContext fetchCtx) throws BatchJobException {
        log.info("========= begin to download ===========");
        JobContext jobCtx = JobContext.builder().build();
        jobCtx.convertFrom(fetchCtx);
        URL url = SpringBatchJobController.class.getResource("/demo/demoSftpProperties.properties");
        SftpTemplate sftpTemplate = appCtx.getBean(SftpTemplate.class);
        sftpTemplate.setJobCtx(jobCtx);
        sftpTemplate.setSftpPropertyFile(url.getFile());
        sftpTemplate.download();
        log.info("========= end download ===========");
    }
}
