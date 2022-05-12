package com.ehi.batch.core.exception;

/**
 * @author portz
 * @date 05/10/2022 15:18
 */
public class SSHException extends RuntimeException {

    public SSHException (String message) {
        super(message);
    }
    public SSHException (String message, Throwable e) {
        super(message, e);
    }
}
