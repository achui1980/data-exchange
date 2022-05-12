package com.ehi.batch.core.connector.sftp;

import com.jcraft.jsch.ChannelSftp;

import java.io.File;

/**
 * @author portz
 * @date 05/10/2022 16:19
 */
public class DefaultSftpOperationListener implements SftpOperationListener {
    @Override
    public void beforeSftpOperation(ChannelSftp.LsEntry file) {

    }

    @Override
    public void afterSftpOperation(File file) {

    }
}
