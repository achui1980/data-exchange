package com.ehi.batch.producer.listener;

import com.ehi.batch.JobStatus;
import com.ehi.batch.producer.core.context.JobContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.batch.core.job.JobParameters;
import org.jeasy.batch.core.job.JobReport;
import org.jeasy.batch.core.listener.JobListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author portz
 * @date 05/18/2022 21:34
 */
@Component
@Slf4j
public class BatchJobListener implements JobListener, DisposableBean {
    public static final String KEY_PREFIX = "producer-";

    @Autowired
    KafkaSender sender;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Setter
    JobContext jobCtx;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public void beforeJob(JobParameters jobParameters) {
        sender.sendKafkaJobFlag(topic, "Batch Start", jobCtx, JobStatus.START);
    }

    @Override
    public void afterJob(JobReport jobReport) {
        sender.sendKafkaJobFlag(topic, "Batch Complete", jobCtx, JobStatus.COMPLETED);
    }

    @Override
    public void destroy() throws Exception {
        Gson gson = new GsonBuilder().create();
        String key = KEY_PREFIX + this.jobCtx.getRequestToken();
        redisTemplate.opsForValue().set(key, gson.toJson(jobCtx));
        redisTemplate.expire(key, calDurationBetweenTodayStartAndEnd());
    }

    private Duration calDurationBetweenTodayStartAndEnd() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return Duration.between(currentTime, todayEnd);
    }
}
