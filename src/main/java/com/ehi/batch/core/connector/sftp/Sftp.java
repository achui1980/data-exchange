package com.ehi.batch.core.connector.sftp;

import lombok.*;

/**
 * @author portz
 * @date 05/10/2022 16:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sftp {
    private String host;
    private int port;
    private String userName;
    @ToString.Exclude
    private String password;
    private String privateKey;
    @ToString.Exclude
    private String privateKeyPassword;
    private String fileFilterRegex;
    private int timeout;
    @ToString.Exclude
    private String folder;
    @ToString.Exclude
    private String archiveFolder;
}
