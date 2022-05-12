package com.ehi.batch.example;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.core.io.ClassPathResource;

/**
 * @author portz
 * @date 05/09/2022 11:06
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
