package com.ehi.batch.producer.core.reader;

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
    private Character separator = '\t';
    @Setter
    private Character quoteChar = '\'';

    public CSVItemReader(final Path path) {
        this(path, Charset.defaultCharset());
    }

    public CSVItemReader(final Path path, final Charset charset) {
        super(path, charset);
    }

    public CSVItemReader(final Path path, final Charset charset, int skipLines, Character separator, Character quoteChar) {
        super(path, charset);
        this.skipLines = skipLines;
        this.quoteChar = quoteChar;
        this.separator = separator;
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
