package com.ehi.batch;

import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.record.StringRecord;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 04/24/2022 16:20
 */
@Component("SBProcessor")
public class SBProcessor implements RecordProcessor<String, String> {

    @Override
    public Record<String> processRecord(Record<String> record) throws Exception {
        StringRecord outputRecord = new StringRecord(null, record.getPayload().toUpperCase());
        return outputRecord;
    }
}
