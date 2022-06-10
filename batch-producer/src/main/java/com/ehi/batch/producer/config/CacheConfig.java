package com.ehi.batch.producer.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author portz
 * @date 06/10/2022 14:47
 */
@Configuration
@Slf4j
public class CacheConfig {

    @Bean("GuavaCache")
    public Cache<String, String> cache() {
        return CacheBuilder.newBuilder().build();
    }
}
