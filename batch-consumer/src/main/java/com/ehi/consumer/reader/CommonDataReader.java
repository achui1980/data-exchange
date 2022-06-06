package com.ehi.consumer.reader;

import com.google.common.base.Stopwatch;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.util.List;

/**
 * @author portz
 * @date 05/25/2022 17:46
 */
@Slf4j
public class CommonDataReader<T> {
    private char delimiter = '\t';
    private char qualifier = '\'';
    private boolean strictQualifiers;
    private ColumnPositionMappingStrategy<T> strategy;
    private CsvToBean<T> csvToBean;

    /**
     *
     * @param recordClass The target type
     * @param columns     Fields name in the same order as in the delimited record
     */
    public CommonDataReader(Class<T> recordClass, String... columns) {
        this.strategy = new ColumnPositionMappingStrategy<>();
        this.strategy.setType(recordClass);
        this.strategy.setColumnMapping(columns);
        this.csvToBean = new CsvToBean<>();
        this.csvToBean.setMappingStrategy(this.strategy);
    }

    public T convertToBean(String record) throws Exception {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(delimiter)
                .withQuoteChar(qualifier)
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(new StringReader(record))
                .withSkipLines(CSVReader.DEFAULT_SKIP_LINES)
                .withCSVParser(parser)
                .build()) {
            this.csvToBean.setCsvReader(csvReader);
            List<T> list = this.csvToBean.parse();
            return list.get(0);
        }
    }
}
