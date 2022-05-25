package com.ehi.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableRetry
public class BatchConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchConsumerApplication.class, args);
	}

}
