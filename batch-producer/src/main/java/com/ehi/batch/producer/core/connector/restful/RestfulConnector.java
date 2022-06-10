package com.ehi.batch.producer.core.connector.restful;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.listener.DefaultOperationListener;
import com.ehi.batch.producer.core.listener.OperationListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author portz
 * @date 06/10/2022 11:57
 */
@Component("RestfulConnector")
@Slf4j
public class RestfulConnector implements Connector {

    private final List<OperationListener> listeners = Lists.newArrayList(new DefaultOperationListener());

    @Autowired
    private Cache<String, String> guavaCache;

    @Override
    public void download(JobContext ctx) {
        String url = ctx.getActionProps().getStr("connector.restful.api.url") + this.queryParams();
        String method = ctx.getActionProps().getStr("connector.restful.api.method");
        String headerStr = ctx.getActionProps().getStr("connector.restful.api.headers");
        Map<String, String> headers = Splitter.on(";")
                .withKeyValueSeparator(":")
                .split(headerStr);
        HttpRequest req = new HttpRequest(UrlBuilder.ofHttp(url))
                .method(Method.valueOf(method.toUpperCase()));
        req.addHeaders(headers);
        req.body(this.getRequestBody());
        HttpResponse response = req.execute();
        String data = this.handlerJsonResponse(response.body());
        doBefore(listeners, ctx);
        guavaCache.put(ctx.getActionId(), data);
        doAfter(listeners, ctx);

    }

    public String getRequestBody() {
        return "{\n" +
                "  \"partnerId\": \"1273481\",\n" +
                "  \"coverageStartDate\": \"2022-04-01\",\n" +
                "  \"coverageEndDate\": \"2022-04-30\",\n" +
                "  \"sort\": {\n" +
                "    \"by\": \"lastStatusUpdateDate\",\n" +
                "    \"order\": \"asc\"\n" +
                "  },\n" +
                "  \"offset\":0,\n" +
                "  \"limit\": 5\n" +
                "}";
    }

    public String queryParams() {
        return StringUtils.EMPTY;
    }

    public String handlerJsonResponse(String jsonResponse) throws BatchJobException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonResponse).get("records").toString();
        } catch (JsonProcessingException e) {
            throw new BatchJobException("convert to json error", e);
        }
    }

    private void doBefore(List<OperationListener> listeners, JobContext jobCtx) {
        for (OperationListener sftpListener : listeners) {
            sftpListener.beforeOperation(jobCtx);
        }
    }

    private void doAfter(List<OperationListener> listeners, JobContext jobCtx) {
        for (OperationListener sftpListener : listeners) {
            sftpListener.afterOperation(jobCtx);
        }
    }

}
