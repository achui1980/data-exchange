package com.ehi.batch.core.connector.sftp;

import com.ehi.batch.core.connector.Connector;

/**
 * @author portz
 * @date 05/10/2022 14:21
 */
public class DefaultSftpConnector implements Connector {
    @Override
    public void download() {

        SftpTemplate sftpTemplate = new SftpTemplate("");
        sftpTemplate.download();
    }
}
