package com.ehi.batch.producer.processor;

import com.ehi.batch.JobStatus;
import com.ehi.batch.PropertyConstant;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.JobConfiguration;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.processor.AbstractBatchProcessor;
import com.ehi.batch.producer.core.reader.CSVItemReader;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * @date 05/09/2022 9:41
 */
@Component(CSVBatchProcessor.ACTION_ID)
public class CSVBatchProcessor extends AbstractBatchProcessor<String, String> {

    public static final String ACTION_ID = "CSVBatchProcessor";

    Gson gson = new GsonBuilder().create();

    @Autowired
    KafkaSender sender;

    @Value("${spring.kafka.topic}")
    private String topic;

    private RecordReader<String> getReaderBean(JobContext ctx) {
        Path datasource = Paths.get(ctx.getSourceData().toURI());
        return new CSVItemReader(datasource, ctx.getActionProps());
    }

    private RecordWriter<String> getWriterBean(JobContext ctx) {

        return batch -> {
            List<Map<String, String>> headers = Lists.newArrayList();
            for (Record<String> record : batch) {
                String json = gson.toJson(record.getPayload());
                sender.sendKafkaJobFlag(topic, json, ctx, JobStatus.PROCESSING);
            }
        };
    }

    private RecordProcessor<String, String> getItemProcessBean(JobContext ctx) {
        return record -> record;
    }

    @Override
    public JobConfiguration<String, String> config(JobContext ctx) throws BatchJobException {
        String mapperClass = ctx.getActionProps().getStr(PropertyConstant.BATCH_RECORD_OBJECT_MODEL);
        String[] columns = ctx.getActionProps().getStr(PropertyConstant.BATCH_RECORD_OBJECT_COLUMNS).split(",");
        try {
            Class targetClass = Class.forName(mapperClass);
            return JobConfiguration.<String, String>builder()
                    .recordReader(getReaderBean(ctx))
                    .recordWriter(getWriterBean(ctx))
                    .recordMapper(new OpenCsvRecordMapper(targetClass, columns))
                    .build();
        } catch (ClassNotFoundException e) {
            throw new BatchJobException("can not find class " + mapperClass, e);
        }
    }
}
