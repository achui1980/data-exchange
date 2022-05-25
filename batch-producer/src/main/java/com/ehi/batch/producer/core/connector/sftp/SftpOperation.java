package com.ehi.batch.producer.core.connector.sftp;

import com.jcraft.jsch.ChannelSftp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author portz
 * @date 05/10/2022 16:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SftpOperation {
    private ChannelSftp channelSftp;
    private List<SftpOperationListener> sftpListerners;
    private Sftp sftp;
}
