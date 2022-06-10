package com.ehi.batch.producer.core.connector.sftp;

import cn.hutool.extra.ssh.ChannelType;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.setting.dialect.Props;
import com.ehi.batch.exception.SSHException;
import com.ehi.batch.producer.core.listener.DefaultOperationListener;
import com.ehi.batch.producer.core.listener.OperationListener;
import com.ehi.batch.producer.core.annotation.ExecuteTimer;
import com.ehi.batch.producer.core.context.JobContext;
import com.google.common.collect.Lists;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

/**
 * @author portz
 * @date 05/10/2022 15:10
 */
@Slf4j
@Service
public class SftpTemplate {

    @Setter
    private JobContext jobCtx;

    List<ChannelSftp.LsEntry> files = Lists.newArrayList();

    private ChannelSftp.LsEntrySelector filter(String fileNamePatternRegex) {

        return (entry) -> {
            final String entryFilename = entry.getFilename();
            if (entryFilename.equals(".") || entryFilename.equals("..")) {
                return ChannelSftp.LsEntrySelector.CONTINUE;
            }
            if (entry.getAttrs().isDir()) {
                return ChannelSftp.LsEntrySelector.CONTINUE;
            }
            if (entryFilename.startsWith(".")) {
                return ChannelSftp.LsEntrySelector.CONTINUE;
            }
            if (entry.getAttrs().isDir() || entry.getAttrs().isLink()) {
                return ChannelSftp.LsEntrySelector.CONTINUE;
            }
            if (StringUtils.isNotEmpty(fileNamePatternRegex)) {
                final Pattern pattern = Pattern.compile(fileNamePatternRegex);
                if (pattern == null || pattern.matcher(entryFilename).matches()) {
                    files.add(entry);
                }
            } else {
                files.add(entry);
            }
            return ChannelSftp.LsEntrySelector.CONTINUE;
        };
    }

    private Sftp initSftp() {
        Props props = jobCtx.getActionProps();
        return Sftp.builder()
                .host(props.getStr("sftp.host"))
                .port(props.getInt("sftp.port", 22))
                .userName(props.getStr("sftp.username"))
                .password(props.getStr("sftp.password"))
                .privateKey(props.getStr("sftp.private.key", null))
                .privateKeyPassword(props.getStr("sftp.private.key.password", null))
                .fileFilterRegex(props.getStr("file.filter.regex"))
                .timeout(props.getInt("sftp.timeout", 60000))
                .folder(props.getStr("sftp.folder", "."))
                .archiveFolder(props.getStr("archive.folder", "/"))
                .build();
    }

    public void execute(Consumer<SftpOperation> operation, List<OperationListener> sftpListerners) {
        Sftp sftp = initSftp();
        log.info("Sftp info: {}", sftp.toString());
        Session session;
        if (StringUtils.isNotEmpty(sftp.getPrivateKey())) {
            session = JschUtil.openSession(sftp.getHost(), sftp.getPort(), sftp.getUserName(), sftp.getPrivateKey(), sftp.getPrivateKeyPassword().getBytes(StandardCharsets.UTF_8));
        } else {
            session = JschUtil.openSession(sftp.getHost(), sftp.getPort(), sftp.getUserName(), sftp.getPassword(), sftp.getTimeout());
        }
        ChannelSftp channelSftp = (ChannelSftp) JschUtil.openChannel(session, ChannelType.SFTP);
        try {
            SftpOperation sftpOperation = SftpOperation.builder()
                    .channelSftp(channelSftp)
                    .sftpListerners(sftpListerners)
                    .sftp(sftp)
                    .build();
            operation.accept(sftpOperation);

        } catch (SSHException e) {
            throw new SSHException(e.getMessage(), e);
        } finally {
            JschUtil.close(channelSftp);
            JschUtil.close(session);
        }
    }

    @ExecuteTimer
    @Retryable(value = {SSHException.class}, maxAttempts = 3, backoff = @Backoff(delay = 10))
    public void download() {
        this.execute((sftpOperation) -> {
            try {
                ChannelSftp channelSftp = sftpOperation.getChannelSftp();
                channelSftp.cd(sftpOperation.getSftp().getFolder());
                Sftp sftp = sftpOperation.getSftp();
                channelSftp.ls(".", filter(sftpOperation.getSftp().getFileFilterRegex()));
                if (CollectionUtils.isEmpty(files)) {
                    log.debug("No files matched");
                }
                for (ChannelSftp.LsEntry file : files) {
                    jobCtx.setSftpFile(file);
                    doBefore(sftpOperation.getSftpListerners(), jobCtx);
                    File localFile = saveFileToLocal(channelSftp, file.getFilename(), sftp.getArchiveFolder());
                    jobCtx.setSourceData(localFile);
                    doAfter(sftpOperation.getSftpListerners(), jobCtx);
                }
            } catch (SftpException | IOException ex) {
                throw new SSHException("error while handle SFTP operation", ex);
            } finally {
                files.clear();
            }
        }, Lists.newArrayList(new DefaultOperationListener()));
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

    private File saveFileToLocal(ChannelSftp channelSftp, String fileName, String archiveFolder) throws SftpException, IOException {
        String filePath = buildArchiveAbsolutePath(fileName, archiveFolder);
        File outPutFile = new File(filePath);
        outPutFile.getParentFile().mkdirs();
        OutputStream outputStream = new FileOutputStream(outPutFile);
        channelSftp.get(fileName, outputStream);
        log.info("localFile=" + outPutFile.getAbsolutePath());
        return outPutFile;
    }

    private String buildArchiveAbsolutePath(String fileName, String archiveFolder) {
        return archiveFolder + buildArchiveRelativePath(fileName);
    }

    private String buildArchiveRelativePath(String fileName) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();
        return format("/sftp/{0,number,#}/{1,number,00}/{2,number,00}/{3}", year, month, day, fileName);
    }
}
