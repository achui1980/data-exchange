package com.ehi.batch.core;

import lombok.Builder;
import lombok.Data;
import org.jeasy.batch.core.filter.RecordFilter;
import org.jeasy.batch.core.listener.*;
import org.jeasy.batch.core.mapper.RecordMapper;
import org.jeasy.batch.core.marshaller.RecordMarshaller;
import org.jeasy.batch.core.processor.RecordProcessor;
import org.jeasy.batch.core.reader.RecordReader;
import org.jeasy.batch.core.validator.RecordValidator;
import org.jeasy.batch.core.writer.RecordWriter;

/**
 * @author portz
 * @date 05/17/2022 16:45
 */
@Data
@Builder
public class JobConfiguration<I, O> {
    private RecordFilter recordFilter;
    private RecordReader<I> recordReader;
    private RecordMapper recordMapper;
    private RecordValidator recordValidator;
    private RecordProcessor recordProcessor;
    private RecordMarshaller recordMarshaller;
    private RecordWriter<O> recordWriter;
    private int batchSize;
    private JobListener jobListener;
    private BatchListener<O> batchListener;
    private RecordReaderListener<I> recordReaderListener;
    private PipelineListener pipelineListener;
    private RecordWriterListener<O> recordWriterListener;
}
