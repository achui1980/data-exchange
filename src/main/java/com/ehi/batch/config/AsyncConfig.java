package com.ehi.batch.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author portz
 * @date 05/16/2022 13:51
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {
    private static final Integer EVENT_BUS_THREAD_POOL_SIZE = 20;
    private static final Integer REST_CONTROLLER_THREAD_POOL_SIZE = 20;
    private static final Integer REST_CONTROLLER_THREAD_POOL_QUEUE_SIZE = Integer.MAX_VALUE;

    @Bean
    public AsyncEventBus asyncEventBus() {
        AsyncEventBus asyncEventBus = new AsyncEventBus(Executors.newFixedThreadPool(EVENT_BUS_THREAD_POOL_SIZE),
                (exception, ctx) -> {
                    log.error("execute task error: ", exception);
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

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(REST_CONTROLLER_THREAD_POOL_SIZE);
        executor.setMaxPoolSize(REST_CONTROLLER_THREAD_POOL_SIZE);
        executor.setQueueCapacity(REST_CONTROLLER_THREAD_POOL_QUEUE_SIZE);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
}
