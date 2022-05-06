package com.ehi.batch.demo;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 04/24/2022 21:05
 */
public class CSVReader {
    public FlatFileItemReader<String> reader() {
        FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        //Set input file location
        reader.setResource(new ClassPathResource("person.csv"));

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }
}
