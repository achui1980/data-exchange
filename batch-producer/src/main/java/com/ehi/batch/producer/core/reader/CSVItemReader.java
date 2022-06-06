package com.ehi.batch.producer.core.reader;

import cn.hutool.setting.dialect.Props;
import com.ehi.batch.PropertyConstant;
import com.google.common.base.Joiner;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.reader.AbstractFileRecordReader;
import org.jeasy.batch.core.record.Header;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.record.StringRecord;

import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * @author portz
 * @date 05/17/2022 13:53
 */
@Slf4j
public class CSVItemReader extends AbstractFileRecordReader<String> {

    private Iterator<String[]> iterator;
    private long currentRecordNumber;
    @Setter
    private int skipLines = 0;
    @Setter
    private Character separator;
    @Setter
    private Character quoteChar;

    public CSVItemReader(final Path path, Props config) {
        this(path, Charset.defaultCharset());
        Character separator = config.getChar(PropertyConstant.BATCH_CSV_SEPARATOR, '\t');
        Character quoteChar = config.getChar(PropertyConstant.BATCH_CSV_QUOTECHAR, '\'');
        int skipLines = config.getInt(PropertyConstant.BATCH_CSV_SKIP_LINES, 0);
        this.skipLines = skipLines;
        this.quoteChar = quoteChar;
        this.separator = separator;
    }

    private CSVItemReader(final Path path, final Charset charset) {
        super(path, charset);
    }


    @Override
    public void open() throws Exception {
        FileReader reader = new FileReader(path.toFile());
        CSVParserBuilder parserBuilder = new CSVParserBuilder()
                .withSeparator(separator)
                .withQuoteChar(quoteChar);
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(skipLines)
                .withCSVParser(parserBuilder.build())
                .build();
        iterator = csvReader.iterator();
    }

    @Override
    public Record<String> readRecord() throws Exception {
        Header header = new Header(++currentRecordNumber, getDataSourceName(), LocalDateTime.now());
        if (iterator.hasNext()) {
            String[] record = iterator.next();
            //Convert to string separate by 'tab'
            return new StringRecord(header, Joiner.on("\t").join(record));
        }
        return null;
    }

    private String getDataSourceName() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public void close() throws Exception {
    }
}
