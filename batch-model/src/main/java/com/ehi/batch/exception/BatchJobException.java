package com.ehi.batch.exception;

/**
 * @author portz
 * @date 05/16/2022 14:19
 */
public class BatchJobException extends RuntimeException {
    public BatchJobException(String msg) {
        super(msg);
    }
    public BatchJobException(String msg, Throwable e) {
        super(msg, e);
    }
}
