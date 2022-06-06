package com.ehi.batch.producer.core.reader;

import cn.hutool.setting.dialect.Props;
import com.alibaba.excel.EasyExcel;
import com.ehi.batch.PropertyConstant;
import com.ehi.batch.exception.BatchJobException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.reader.AbstractFileRecordReader;
import org.jeasy.batch.core.record.Header;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.record.StringRecord;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

/**
 * @author portz
 * @date 06/02/2022 15:26
 */
@Slf4j
public class ExcelItemReader extends AbstractFileRecordReader<String> {

    private Iterator iterator;
    private long currentRecordNumber;
    private Class targetClass;

    private int sheetIndex;
    private int headerIndex;

    private Gson gson = new GsonBuilder().create();

    public ExcelItemReader(Path path, Props config) {
        super(path);
        this.sheetIndex = config.getInt(PropertyConstant.BATCH_EXCEL_SHEET_INDEX, 0);
        this.headerIndex = config.getInt(PropertyConstant.BATCH_EXCEL_HEADER_INDEX, 1);
        String model = config.getStr(PropertyConstant.BATCH_RECORD_OBJECT_MODEL);
        try {
            this.targetClass = Class.forName(model);
        } catch (ClassNotFoundException ex) {
            throw new BatchJobException("unable to find class: " + model, ex);
        }
    }


    @Override
    public void open() throws Exception {
        List list = EasyExcel.read(path.toFile())
                .sheet(this.sheetIndex)
                .headRowNumber(this.headerIndex)
                .head(targetClass)
                .doReadSync();
        iterator = list.iterator();
    }

    @Override
    public Record<String> readRecord() throws Exception {
        Header header = new Header(++currentRecordNumber, getDataSourceName(), LocalDateTime.now());
        if (iterator.hasNext()) {
            String record = gson.toJson(iterator.next());
            return new StringRecord(header, record);
        }
        return null;
    }

    private String getDataSourceName() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
