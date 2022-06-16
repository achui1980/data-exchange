package com.ehi.batch.producer.core.connector.restful;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.connector.Connector;
import com.ehi.batch.producer.core.connector.DownloadData;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.core.listener.DefaultOperationListener;
import com.ehi.batch.producer.core.listener.KafkaSendFlagListener;
import com.ehi.batch.producer.core.listener.OperationListener;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author portz
 * @date 06/10/2022 11:57
 */
@Component("RestfulConnector")
@Slf4j
public abstract class MultipleRestfulConnector implements Connector {
    @Autowired
    private Cache<String, String> guavaCache;

    @Autowired
    KafkaSendFlagListener kafkaSendFlagListener;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    private List<ListenableFuture<Boolean>> futureList = Lists.newArrayList();
    private List<OperationListener> listeners;

    @PostConstruct
    public void init() {
        listeners = Lists.newArrayList(new DefaultOperationListener());
    }

    @Override
    public void download(JobContext ctx) {
        DownloadData downloadData = firstFetch(ctx, 0);
        String responseData = downloadData.getData();
        Integer times = this.times(responseData);
        kafkaSendFlagListener.beforeOperation(ctx);
        for (int i = 0; i < times; i++) {
            int batch = i;
            JobContext cloneCtx = SerializationUtils.clone(ctx);
            ListenableFuture<Boolean> future = executorService.submit(() -> {
                DownloadData data = this.firstFetch(cloneCtx, batch);
                cloneCtx.setBatch(String.valueOf(batch));
                doBefore(listeners, cloneCtx);
                guavaCache.put(cloneCtx.getActionId() + batch, data.getData());
                cloneCtx.setResponse(data.getData());
                doAfter(listeners, cloneCtx);
                return Boolean.TRUE;
            });
            futureList.add(future);
        }
        ListenableFuture<List<Boolean>> executionList = Futures.successfulAsList(futureList);
        try {
            executionList.get();
        } catch (Exception ex) {
            log.error("complete thread pool error ", ex);
        } finally {
            kafkaSendFlagListener.afterOperation(ctx);
        }

    }

    public Integer times(String response) {
        return 100;
    }

    private DownloadData firstFetch(JobContext ctx, Integer batch) {
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
        return DownloadData.builder()
                .currentBatch(batch)
                .actionId(ctx.getActionId())
                .data(data)
                .build();
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
