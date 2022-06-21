package com.ehi.consumer.reciver.handler;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.model.BatchJobReport;
import com.ehi.consumer.reciver.ConsumerJobContext;

/**
 * @author portz
 * @date 05/18/2022 22:13
 */
public interface RecordHandler {

    BatchJobReport processRecord(ConsumerJobContext ctx) throws BatchJobException;
}
