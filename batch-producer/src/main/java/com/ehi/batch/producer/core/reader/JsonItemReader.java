package com.ehi.batch.producer.core.reader;

import cn.hutool.setting.dialect.Props;
import com.ehi.batch.producer.core.ApplicationContextProvider;
import com.ehi.batch.producer.core.context.JobContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.jeasy.batch.core.reader.AbstractFileRecordReader;
import org.jeasy.batch.core.record.Header;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.core.record.StringRecord;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author portz
 * @date 06/10/2022 14:42
 */
public class JsonItemReader extends AbstractFileRecordReader<String> {

    private final Cache<String, String> guavaCache;
    private Iterator<JsonNode> iterator;
    private long currentRecordNumber;
    private JobContext ctx;

    public JsonItemReader(Path path, JobContext ctx) {
        super(path);
        guavaCache = ApplicationContextProvider.getApplicationContext().getBean("GuavaCache", Cache.class);
        this.ctx = ctx;
    }

    @Override
    public void open() throws Exception {
        String cacheJson;
        String batch = Optional.ofNullable(ctx.getBatch()).orElse("0");
        String key = ctx.getActionId() + batch;
        if (path != null) {
            String json = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
            cacheJson = guavaCache.get(key, () -> json);
        } else {
            cacheJson = guavaCache.getIfPresent(key);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(cacheJson);
        if (jsonNode.isArray()) {
            iterator = jsonNode.iterator();
        } else {
            iterator = Lists.newArrayList(jsonNode).iterator();
        }
    }

    @Override
    public Record<String> readRecord() throws Exception {
        Header header = new Header(++currentRecordNumber, getDataSourceName(), LocalDateTime.now());
        if (iterator.hasNext()) {
            String record = iterator.next().toString();
            return new StringRecord(header, record);
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        String batch = Optional.ofNullable(ctx.getBatch()).orElse("0");
        String key = ctx.getActionId() + batch;
        guavaCache.invalidate(key);
    }

    private String getDataSourceName() {
        return path != null ? path.toAbsolutePath().toString() : "memory";
    }

}
