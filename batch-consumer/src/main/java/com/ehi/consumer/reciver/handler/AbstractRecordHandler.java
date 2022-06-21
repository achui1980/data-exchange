package com.ehi.consumer.reciver.handler;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.model.BatchJobMetric;
import com.ehi.batch.model.BatchJobReport;
import com.ehi.consumer.reciver.ConsumerJobContext;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author portz
 * @date 05/19/2022 19:35
 */
@Slf4j
public abstract class AbstractRecordHandler implements RecordHandler {

    Stopwatch sw;
    private Cache<String, ListeningExecutorService> threadPoolCache = CacheBuilder.newBuilder().build();
    private Cache<String, List<ListenableFuture<Boolean>>> listCache = CacheBuilder.newBuilder().build();
    private BatchJobReport report;
    private BatchJobMetric metrics;
    private int totalCount = 0;

    @PostConstruct
    public void init() {

    }

    @Override
    public BatchJobReport processRecord(ConsumerJobContext ctx) throws BatchJobException {
        String actionId = ctx.getMessageMeta().getActionId();
        boolean isJobComplete = ctx.getMessageMeta().isJobComplete();
        boolean isJobStart = ctx.getMessageMeta().isJobStart();
        ListeningExecutorService executorService;
        List<ListenableFuture<Boolean>> executeResultList;
        try {
            executorService = threadPoolCache.get(actionId, () -> MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5)));
            executeResultList = listCache.get(actionId, () -> Lists.newArrayList());
        } catch (ExecutionException ex) {
            throw new BatchJobException("create cache error", ex);
        }

        if (isJobStart) {
            sw = Stopwatch.createUnstarted();
            sw.start();

        }
        if (!(isJobStart || isJobComplete)) {
            executeResultList.add(executorService.submit(() -> {
                handleEachRecord(ctx);
                return Boolean.TRUE;
            }));
            totalCount++;
        }
        if (isJobComplete) {
            int successCount = -1;
            try {
                successCount = completeJob(executeResultList, executorService, ctx);
            } catch (Exception e) {
                throw new BatchJobException("complete job error ", e);
            } finally {
                sw.stop();
                log.warn(" complete {} in {}", totalCount, sw);
                createMetrics(successCount, ctx);
                totalCount = 0;
            }
        }
        return this.report;
    }

    private void createMetrics(int successCount, ConsumerJobContext ctx) {
        this.report = new BatchJobReport();
        this.metrics = new BatchJobMetric();
        report.setActionId(ctx.getMessageMeta().getActionId());
        report.setRequestToken(ctx.getMessageMeta().getRequestToken());
        this.metrics.setProcessTime(sw.toString());
        this.metrics.setDuration(sw.elapsed(TimeUnit.MILLISECONDS));
        this.metrics.setTotalCount(totalCount);
        this.metrics.setSuccessCount(successCount);
        this.metrics.setFailureCount(totalCount - successCount);
        this.report.setMetrics(metrics);
    }

    private Integer completeJob(List<ListenableFuture<Boolean>> executeResultList, ListeningExecutorService executorService, ConsumerJobContext ctx) throws Exception {
        ListenableFuture<List<Boolean>> executionList = Futures.successfulAsList(executeResultList);
        //waiting for all tasks complete
        List<Boolean> result = executionList.get();
        closeResource(executeResultList, executorService, ctx);
        whenJobComplete(ctx);
        return result.size();
    }

    /**
     * process logic when job complete (e.g. write to nfs)
     *
     * @param ctx
     */
    public abstract void whenJobComplete(ConsumerJobContext ctx);

    /**
     * process logic of each record (e.g. call api, write to storage)
     *
     * @param ctx
     */
    public abstract void handleEachRecord(ConsumerJobContext ctx);

    private void closeResource(List<ListenableFuture<Boolean>> executeResultList, ListeningExecutorService executorService, ConsumerJobContext ctx) {
        String actionId = ctx.getMessageMeta().getActionId();
        executorService.shutdown();
        threadPoolCache.invalidate(actionId);
        executeResultList.clear();
        listCache.invalidate(actionId);
    }
}
