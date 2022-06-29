package com.ehi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableRetry
@Slf4j
public class BatchConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchConsumerApplication.class, args);
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> log.info("application shutdown due to release or exception")));
    }

}
