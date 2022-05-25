package com.ehi.batch.producer.example;

import com.ehi.batch.model.MessageHeader;
import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.processor.AbstractBatchProcessor;
import com.ehi.batch.producer.core.reader.CSVItemReader;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.record.Batch;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author portz
 * @date 05/09/2022 9:41
 */
@Component(CSVBatchProcessor.ACTION_ID)
public class CSVBatchProcessor extends AbstractBatchProcessor<String[], String[]> {

    public static final String ACTION_ID = "CSVBatchProcessor";

    @Autowired
    KafkaSender sender;

    private RecordReader<String[]> getReaderBean(JobContext ctx) {
        Path datasource = Paths.get(ctx.getSourceData().toURI());
        Character separator = ctx.getActionProps().getChar("batch.csv.separator", '\t');
        Character quoteChar = ctx.getActionProps().getChar("batch.csv.quoteChar", '\'');
        int skipLines = ctx.getActionProps().getInt("batch.cvs.skip.lines", 0);
        return new CSVItemReader(datasource, Charset.defaultCharset(), skipLines, separator, quoteChar);
    }

    private RecordWriter<String[]> getWriterBean(JobContext ctx) {

        return new RecordWriter<String[]>() {
            @Override
            public void writeRecords(Batch<String[]> batch) throws Exception {
                List<Map<String, String>> headers = Lists.newArrayList();
                for (Record<String[]> record : batch) {
                    Map<String, String> header = Maps.newHashMap();
                    MessageHeader messageHeader = MessageHeader.builder()
                            .actionId(ctx.getActionId())
                            .rowNumber(record.getHeader().getNumber())
                            .mapperClass(ctx.getActionProps().getStr("batch.record.mapper.class"))
                            .requestToken(ctx.getRequestToken())
                            .build();
                    header.put("X-Batch-Meta-Json", messageHeader.toString());
                    headers.add(header);
                    sender.send("port.test", ctx.getActionId(), record.getPayload().toString(), headers);
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
    public JobConfiguration<String[], String[]> config(JobContext ctx) {
        return JobConfiguration.<String[], String[]>builder()
                .recordReader(getReaderBean(ctx))
                .recordWriter(getWriterBean(ctx))
                .recordProcessor(getItemProcessBean(ctx))
                .build();
    }
}
