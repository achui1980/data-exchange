package com.ehi.batch.consumer;

import com.ehi.batch.WritetoNFS;
import com.ehi.batch.core.exception.BatchJobException;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author portz
 * @date 05/19/2022 19:35
 */
@Slf4j
public abstract class AbstractRecordHandler implements RecordHandler {

    Stopwatch sw;
    private Map<String, ListeningExecutorService> threadPoolMap = Maps.newConcurrentMap();
    private Map<String, List<ListenableFuture<Boolean>>> listMap = Maps.newConcurrentMap();
    private int count = 0;

    @Override
    public void processRecord(ConsumerJobContext ctx) throws BatchJobException {
        String actionId = ctx.getMessageMeta().getActionId();
        boolean isJobComplete = ctx.getMessageMeta().isJobComplete();
        boolean isJobStart = ctx.getMessageMeta().isJobStart();
        ListeningExecutorService executorService = threadPoolMap.get(actionId);
        List<ListenableFuture<Boolean>> executeResultList = listMap.get(actionId);
        if (executorService == null) {
            executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
            threadPoolMap.put(actionId, executorService);
        }
        if (executeResultList == null) {
            executeResultList = Lists.newArrayList();
            listMap.put(actionId, executeResultList);
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
            count++;
            //listMap.put(actionId, list);
        }
        if (isJobComplete) {
            try {
                completeJob(executeResultList, executorService, ctx);
            } catch (Exception e) {
                throw new BatchJobException("complete job error ", e);
            } finally {
                sw.stop();
                log.warn(" complete {} in {}", count, sw);
            }
        }
    }

    private void completeJob(List<ListenableFuture<Boolean>> executeResultList, ListeningExecutorService executorService, ConsumerJobContext ctx) throws Exception {
        ListenableFuture<List<Boolean>> executionList = Futures.successfulAsList(executeResultList);
        //waiting for all tasks complete
        executionList.get();
        closeResource(executeResultList, executorService, ctx);
        whenJobComplete(ctx);
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
        threadPoolMap.remove(actionId);
        executeResultList.clear();
        listMap.remove(actionId);
    }
}
