package com.ehi.consumer.reciver;

import com.ehi.batch.exception.BatchJobException;

/**
 * @author portz
 * @date 05/18/2022 22:13
 */
public interface RecordHandler {

    void processRecord(ConsumerJobContext ctx) throws BatchJobException;
}
