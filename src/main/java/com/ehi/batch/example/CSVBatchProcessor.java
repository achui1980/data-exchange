package com.ehi.batch.example;

import com.ehi.batch.core.context.JobContext;
import com.ehi.batch.core.processor.DefaultBatchProcessor;
import com.ehi.batch.kafka.KafkaSender;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;
import org.jeasy.batch.flatfile.FlatFileRecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author portz
 * @date 05/09/2022 9:41
 */
@Component("CSVBatchProcessor")
public class CSVBatchProcessor extends DefaultBatchProcessor<String, String> {

    @Autowired
    KafkaOutput writer;

    @Autowired
    KafkaSender sender;

    @Override
    public RecordReader<String> getReaderBean(JobContext ctx) {
        Path datasource = Paths.get(ctx.getSourceData().toURI());
        return new FlatFileRecordReader(datasource);
    }

    @Override
    public RecordWriter<String> getWriterBean(JobContext ctx) {

        return new RecordWriter<String>() {
            @Override
            public void writeRecords(Batch<String> batch) throws Exception {
                for (Record<String> record : batch) {
                    sender.send("port.test", record.getPayload(), null);
                }
            }
        };
    }

    @Override
    public RecordProcessor<String, String> getItemProcessBean(JobContext ctx) {
        return new RecordProcessor<String, String>() {
            @Override
            public Record<String> processRecord(Record<String> record) throws Exception {
                return record;
            }
        };
    }


//    @Override
//    public List<JobExecutionListener> listeners(JobContext ctx) {
//        return Lists.newArrayList(new SpringBatchJobCompletionListener());
//    }
//
//    @Override
//    public ItemReader<String> getReaderBean(JobContext ctx) {
//        return new CSVReader(ctx).reader();
//    }
//
//    @Override
//    public ItemWriter<String> getWriterBean(JobContext ctx) {
//        return writer;
//    }
//
//    @Override
//    public ItemProcessor getItemProcessBean(JobContext ctx) {
//        return new SBProcessor();
//    }
}
