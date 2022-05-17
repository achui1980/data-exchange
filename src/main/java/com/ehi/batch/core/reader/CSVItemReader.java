package com.ehi.batch.core.reader;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;

/**
 * @author portz
 * @date 05/17/2022 13:53
 */
@Slf4j
public class CSVItemReader implements ItemReader<String>{
    private String filePath = "";
    @Override
    public String read() {
        //CSVReader reader = new CSVReaderBuilder(new FileReader(filePath));
        return null;
    }
}
