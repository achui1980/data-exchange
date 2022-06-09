package com.ehi.consumer.reciver;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.model.BatchJobReport;

/**
 * @author portz
 * @date 05/18/2022 22:13
 */
public interface RecordHandler {

    BatchJobReport processRecord(ConsumerJobContext ctx) throws BatchJobException;
}
