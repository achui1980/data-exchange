package com.ehi.batch.producer.example;

import com.ehi.batch.PropertyConstant;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.model.MessageHeader;
import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.processor.AbstractBatchProcessor;
import com.ehi.batch.producer.core.reader.JsonItemReader;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.ehi.batch.producer.listener.DummyJobListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.extern.slf4j.Slf4j;
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
 * @date 06/10/2022 14:37
 */
@Component("JsonBatchProcessor")
@Slf4j
public class JsonBatchProcessor extends AbstractBatchProcessor<String, String> {
    public static final String ACTION_ID = "JsonBatchProcessor";
    @Autowired
    KafkaSender sender;

    @Value("${spring.kafka.topic}")
    private String topic;


    @Autowired
    private ListeningExecutorService kafkaSenderThreadPool;

    private RecordReader<String> getReaderBean(JobContext ctx) {
        Path datasource = null;
        if (ctx.getSourceData() != null) {
            datasource = Paths.get(ctx.getSourceData().toURI());
        }
        JsonItemReader jsonItemReader = new JsonItemReader(datasource, ctx);
        return jsonItemReader;
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

    @Override
    public JobConfiguration<String, String> config(JobContext ctx) throws BatchJobException {
        return JobConfiguration.<String, String>builder()
                .recordReader(getReaderBean(ctx))
                .recordWriter(getWriterBean(ctx))
                .jobListener(new DummyJobListener())
                .build();
    }
}
