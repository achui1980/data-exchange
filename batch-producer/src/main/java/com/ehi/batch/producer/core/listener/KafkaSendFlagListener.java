package com.ehi.batch.producer.core.listener;


import com.ehi.batch.JobStatus;
import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.ehi.batch.producer.listener.BatchJobListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author portz
 * @date 06/15/2022 17:24
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaSendFlagListener implements OperationListener, DisposableBean {
    @Autowired
    private KafkaSender kafkaSender;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private JobContext jobCtx;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public void beforeOperation(JobContext ctx) throws BatchJobException {
        this.jobCtx = ctx;
        kafkaSender.sendKafkaJobFlag(topic, "Batch Start", ctx, JobStatus.START);
    }

    @Override
    public void afterOperation(JobContext ctx) throws BatchJobException {
        this.jobCtx = ctx;
        kafkaSender.sendKafkaJobFlag(topic, "Batch Complete", ctx, JobStatus.COMPLETED);
    }

    @Override
    public void destroy() throws Exception {
        if (jobCtx == null) {
            return;
        }
        Gson gson = new GsonBuilder().create();
        String key = BatchJobListener.KEY_PREFIX + this.jobCtx.getRequestToken();
        redisTemplate.opsForValue().set(key, gson.toJson(jobCtx));
        redisTemplate.expire(key, calDurationBetweenTodayStartAndEnd());
    }

    private Duration calDurationBetweenTodayStartAndEnd() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return Duration.between(currentTime, todayEnd);
    }
}
