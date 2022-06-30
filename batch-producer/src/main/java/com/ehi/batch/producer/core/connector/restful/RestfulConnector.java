package com.ehi.batch.producer.core.connector.restful;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.annotation.ExecuteTimer;
import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.listener.DefaultOperationListener;
import com.ehi.batch.producer.core.listener.KafkaSendFlagListener;
import com.ehi.batch.producer.core.listener.OperationListener;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author portz
 * @date 06/10/2022 11:57
 */
@Component("RestfulConnector")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public abstract class RestfulConnector implements Connector {

    @Autowired
    KafkaSendFlagListener kafkaSendFlagListener;
    private List<OperationListener> listeners;

    @Autowired
    private Cache<String, String> guavaCache;

    @PostConstruct
    public void init() {
        listeners = Lists.newArrayList(new DefaultOperationListener());
    }

    @Override
    @ExecuteTimer
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
        String batch = Optional.ofNullable(ctx.getBatch()).orElse("0");
        String key = ctx.getActionId() + batch;
        kafkaSendFlagListener.beforeOperation(ctx);
        doBefore(listeners, ctx);
        guavaCache.put(key, data);
        ctx.setResponse(data);
        doAfter(listeners, ctx);
        kafkaSendFlagListener.afterOperation(ctx);

    }

    public abstract String getRequestBody();

    public abstract String queryParams();

    public abstract String handlerJsonResponse(String jsonResponse) throws BatchJobException;

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
