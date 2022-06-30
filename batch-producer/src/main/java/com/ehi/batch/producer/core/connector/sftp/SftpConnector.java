package com.ehi.batch.producer.core.connector.sftp;

import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.connector.DownloadData;
import com.ehi.batch.producer.core.context.JobContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 05/10/2022 14:21
 */
@Component("SftpConnector")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SftpConnector implements Connector {
    @Autowired
    SftpTemplate sftpTemplate;

    @Override
    public void download(JobContext ctx) {
        sftpTemplate.setJobCtx(ctx);
        sftpTemplate.download();
    }
}
