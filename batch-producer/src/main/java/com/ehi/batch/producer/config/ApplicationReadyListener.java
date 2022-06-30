package com.ehi.batch.producer.config;

import com.ehi.batch.JobStatus;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.ehi.batch.producer.listener.BatchJobListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author portz
 * @date 06/29/2022 15:48
 */
@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    KafkaSender sender;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> keys = redisTemplate.keys(BatchJobListener.KEY_PREFIX+"*");
        if(CollectionUtils.isEmpty(keys)) {
            return;
        }
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        if(CollectionUtils.isEmpty(values)) {
            return;
        }
        redisTemplate.delete(keys);
        Gson gson = new GsonBuilder().create();
        values.forEach(v -> {
            JobContext jobCtx = gson.fromJson(v, JobContext.class);
            sender.sendKafkaJobFlag(topic, "Batch Complete", jobCtx, JobStatus.COMPLETED);
        });
    }
}
