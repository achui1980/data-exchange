package com.ehi.batch.producer.example;

import com.google.common.base.Stopwatch;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.mapper.RecordMapper;
import org.jeasy.batch.core.record.GenericRecord;
import org.jeasy.batch.core.record.Record;

import java.io.StringReader;
import java.util.List;

/**
 * @author portz
 * @date 05/25/2022 17:00
 */
@Slf4j
public class OpenCsvRecordMapper<T> implements RecordMapper<String, T> {
    private char delimiter = '\t';
    private char qualifier = '\'';
    private ColumnPositionMappingStrategy<T> strategy;
    private CsvToBean<T> csvToBean;

    public OpenCsvRecordMapper(Class<T> recordClass, String... columns) {
        this.strategy = new ColumnPositionMappingStrategy<>();
        this.strategy.setType(recordClass);
        this.strategy.setColumnMapping(columns);
        this.csvToBean = new CsvToBean<>();
        this.csvToBean.setMappingStrategy(this.strategy);
    }

    @Override
    public Record<T> processRecord(Record<String> record) throws Exception {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(delimiter)
                .withQuoteChar(qualifier)
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(new StringReader(record.getPayload()))
                .withSkipLines(CSVReader.DEFAULT_SKIP_LINES)
                .withCSVParser(parser)
                .build()) {
            this.csvToBean.setCsvReader(csvReader);
            List<T> list = this.csvToBean.parse();
            T object = list.get(0);
            return new GenericRecord<>(record.getHeader(), object);
        }
    }
}
