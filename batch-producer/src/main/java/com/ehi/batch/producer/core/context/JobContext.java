package com.ehi.batch.producer.core.context;

import com.jcraft.jsch.ChannelSftp;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.File;

/**
 * @author portz
 * @date 05/13/2022 16:50
 */
@Data
@SuperBuilder(toBuilder = true)
public class JobContext extends Context {
    private File sourceData;
    private String response;
    private ChannelSftp.LsEntry sftpFile;

    public JobContext convertFrom(FetchContext ctx) {
        super.setActionId(ctx.getActionId());
        super.setRequestToken(ctx.getRequestToken());
        super.setActionProps(ctx.getActionProps());
        return this;
    }
}
