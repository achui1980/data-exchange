package com.ehi.batch.example;

import com.ehi.batch.SBProcessor;
import com.ehi.batch.SpringBatchJobCompletionListener;
import com.ehi.batch.core.processor.AbstractBatchProcessor;
import com.google.common.collect.Lists;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author portz
 * @date 05/09/2022 9:41
 */
@Component("CSVBatchProcessor")
public class CSVBatchProcessor extends AbstractBatchProcessor<String, String> {

    @Autowired
    KafkaOutput writer;

    @Override
    public List<JobExecutionListener> listeners() {
        return Lists.newArrayList(new SpringBatchJobCompletionListener());
    }

    @Override
    public ItemReader<String> getReaderBean() {
        return new CSVReader().reader();
    }

    @Override
    public ItemWriter<String> getWriterBean() {
        return writer;
    }

    @Override
    public ItemProcessor getItemProcessBean() {
        return new SBProcessor();
    }
}
