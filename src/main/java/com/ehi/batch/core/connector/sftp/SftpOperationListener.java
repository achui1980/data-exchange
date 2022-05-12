package com.ehi.batch.core.connector.sftp;

import com.jcraft.jsch.ChannelSftp;

import java.io.File;

/**
 * @author portz
 * @date 05/10/2022 16:08
 */
public interface SftpOperationListener {
    void beforeSftpOperation(ChannelSftp.LsEntry file);

    void afterSftpOperation(File file);
}
