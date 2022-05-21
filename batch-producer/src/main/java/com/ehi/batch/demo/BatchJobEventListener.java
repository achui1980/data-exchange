package com.ehi.batch.demo;

import com.ehi.batch.kafka.Message;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.Executors;

/**
 * @author portz
 * @date 05/16/2022 13:30
 */
public class BatchJobEventListener {
    @Subscribe
    public void listenerJob(Message context) {
        System.out.println("EventListener#listenInteger ->" + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(100));
        eventBus.register(new BatchJobEventListener());
        Message message = new Message();
        message.setId(RandomUtils.nextLong());
        message.setMsg("Hello World");
        eventBus.post(message);
        eventBus.post(message);
        eventBus.post(message);
    }
}
