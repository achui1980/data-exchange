package com.ehi.batch.producer.example;

import com.ehi.batch.PropertyConstant;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.model.MessageHeader;
import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.processor.AbstractBatchProcessor;
import com.ehi.batch.producer.core.reader.ExcelItemReader;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.writer.RecordWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author portz
 * @date 06/02/2022 15:26
 */
@Component(ExcelBatchProcessor.ACTION_ID)
public class ExcelBatchProcessor extends AbstractBatchProcessor<String, String> {
    public static final String ACTION_ID = "ExcelBatchProcessor";
    @Autowired
    KafkaSender sender;

    @Value("${spring.kafka.topic}")
    private String topic;

    private RecordReader<String> getReaderBean(JobContext ctx) {
        Path datasource = Paths.get(ctx.getSourceData().toURI());
        ExcelItemReader excelItemReader = new ExcelItemReader(datasource, ctx.getActionProps());
        return excelItemReader;
    }

    private RecordWriter<String> getWriterBean(JobContext ctx) {

        return batch -> {
            List<Map<String, String>> headers = Lists.newArrayList();
            for (Record<String> record : batch) {
                Map<String, String> header = Maps.newHashMap();
                MessageHeader messageHeader = MessageHeader.builder()
                        .actionId(ctx.getActionId())
                        .rowNumber(record.getHeader().getNumber())
                        .objectModel(ctx.getActionProps().getStr(PropertyConstant.BATCH_RECORD_OBJECT_MODEL))
                        .requestToken(ctx.getRequestToken())
                        .build();
                header.put("X-Batch-Meta-Json", messageHeader.toString());
                headers.add(header);
                sender.send(topic, ctx.getActionId(), record.getPayload(), headers);
            }
        };
    }

    private RecordProcessor<String, String> getItemProcessBean(JobContext ctx) {
        return record -> record;
    }

    @Override
    public JobConfiguration<String, String> config(JobContext ctx) throws BatchJobException {
        return JobConfiguration.<String, String>builder()
                .recordReader(getReaderBean(ctx))
                .recordWriter(getWriterBean(ctx))
                .recordProcessor(getItemProcessBean(ctx))
                .build();
    }
}
