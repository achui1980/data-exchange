package com.ehi.batch.producer.core.connector.file;

import com.ehi.batch.producer.core.listener.OperationListener;
import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.listener.DefaultOperationListener;
import com.ehi.batch.producer.core.context.JobContext;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author portz
 * @date 06/09/2022 16:33
 */
@Component("FileConnector")
@Slf4j
public class FileConnector implements Connector {
    private final List<OperationListener> listeners = Lists.newArrayList(new DefaultOperationListener());

    @Override
    public void download(JobContext ctx) {
        String folder = ctx.getActionProps().getStr("archive.folder");
        String nameFilter = ctx.getActionProps().getStr("file.filter.regex");
        Assert.notNull(folder, "property archive.folder should not be configured");
        File dir = new File(folder);
        File[] files = dir.listFiles((file) -> {
            final Pattern pattern = Pattern.compile(nameFilter);
            return StringUtils.isEmpty(nameFilter) ?
                    Boolean.TRUE : pattern.matcher(file.getName()).matches();
        });
        if (files == null || files.length == 0) {
            log.info("not file found");
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        DefaultOperationListener listener = new DefaultOperationListener();
        for (File file : files) {
            doBefore(listeners, ctx);
            ctx.setSourceData(file);
            doAfter(listeners, ctx);
        }
    }

    private void doBefore(List<OperationListener> listeners, JobContext jobCtx) {
        for (OperationListener sftpListener : listeners) {
            sftpListener.beforeOperation(jobCtx);
        }
    }

    private void doAfter(List<OperationListener> listeners, JobContext jobCtx) {
        for (OperationListener sftpListener : listeners) {
            sftpListener.afterOperation(jobCtx);
        }
    }
}
