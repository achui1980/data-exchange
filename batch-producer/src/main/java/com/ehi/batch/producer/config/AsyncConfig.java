package com.ehi.batch.producer.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * @author portz
 * @date 05/16/2022 13:51
 */
@Configuration
@Slf4j
public class AsyncConfig {
    private static final Integer EVENT_BUS_THREAD_POOL_SIZE = 20;
    private static final Integer COMMON_THREAD_POOL_SIZE = 100;

    @Bean("asyncEventBus")
    public AsyncEventBus asyncEventBus() {
        AsyncEventBus asyncEventBus = new AsyncEventBus(eventBusThreadPool(),
                (exception, ctx) -> {
                    log.error("async execute task error: ", exception);
                });
        //register dead event.
        asyncEventBus.register(new Object() {
            @Subscribe
            public void lister(DeadEvent event) {
                log.error("{}={} from dead events", event.getSource().getClass(), event.getEvent());
            }
        });
        return asyncEventBus;
    }

    @Bean("eventBus")
    public EventBus eventBus() {
        return new EventBus((exception, ctx) -> {
            log.error("execute task error: ", exception);
        });
    }

    private ListeningExecutorService eventBusThreadPool() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(EVENT_BUS_THREAD_POOL_SIZE));
    }


    @Bean
    public ListeningExecutorService commonThreadPool() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(COMMON_THREAD_POOL_SIZE));
    }
}
