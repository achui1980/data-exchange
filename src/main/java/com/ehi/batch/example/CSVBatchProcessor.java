package com.ehi.batch.example;

import com.ehi.batch.core.JobConfiguration;
import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.processor.AbstractBatchProcessor;
import com.ehi.batch.kafka.KafkaSender;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;
import org.jeasy.batch.flatfile.FlatFileRecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author portz
 * @date 05/09/2022 9:41
 */
@Component(CSVBatchProcessor.ACTION_ID)
public class CSVBatchProcessor extends AbstractBatchProcessor<String, String> {

    public static final String  ACTION_ID = "CSVBatchProcessor";

    @Autowired
    KafkaSender sender;

    private RecordReader<String> getReaderBean(JobContext ctx) {
        Path datasource = Paths.get(ctx.getSourceData().toURI());
        return new FlatFileRecordReader(datasource);
    }

    private RecordWriter<String> getWriterBean(JobContext ctx) {

        return new RecordWriter<String>() {
            @Override
            public void writeRecords(Batch<String> batch) throws Exception {
                for (Record<String> record : batch) {
                    sender.send("port.test", record.getPayload(), null);
                }
            }
        };
    }

    private RecordProcessor<String, String> getItemProcessBean(JobContext ctx) {
        return new RecordProcessor<String, String>() {
            @Override
            public Record<String> processRecord(Record<String> record) throws Exception {
                return record;
            }
        };
    }

    @Override
    public JobConfiguration<String, String> config(JobContext ctx) {
        return JobConfiguration.<String, String>builder()
                .recordReader(getReaderBean(ctx))
                .recordWriter(getWriterBean(ctx))
                .recordProcessor(getItemProcessBean(ctx))
                .build();
    }
}
