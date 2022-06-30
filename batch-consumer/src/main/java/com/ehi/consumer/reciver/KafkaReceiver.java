package com.ehi.consumer.reciver;

import cn.hutool.setting.dialect.Props;
import com.ehi.batch.JobStatus;
import com.ehi.batch.PropertyConstant;
import com.ehi.batch.model.BatchJobReport;
import com.ehi.batch.model.MessageHeader;
import com.ehi.consumer.reciver.handler.RecordHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author portz
 * @date 04/24/2022 20:59
 */
@Component
@Slf4j
public class KafkaReceiver implements DisposableBean {

    private static final String KEY_PREFIX = "consumer-";

    @Autowired
    ApplicationContext appCtx;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${application.data.resources}")
    private String resourceFolder;

    private Gson gson;
    private Cache<String, Props> propertyCache;
    private Cache<String, MessageHeader> actionIdCache;

    @PostConstruct
    public void register() {
        gson = new GsonBuilder().create();
        propertyCache = CacheBuilder.newBuilder()
                .build();
        actionIdCache = CacheBuilder.newBuilder()
                .build();

    }

    @KafkaListener(topics = {"${spring.kafka.topic}"})
    public void listen(ConsumerRecord<?, ?> record, @Header("X-Batch-Meta-Json") String metaJson) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                MessageHeader messageMeta = gson.fromJson(metaJson, MessageHeader.class);
                JobStatus jobStatus = messageMeta.getJobStatus();
                if (ignoreMessageIfNeeded(messageMeta)) {
                    if (JobStatus.COMPLETED == jobStatus) {
                        actionIdCache.invalidate(messageMeta.getActionId());
                    }
                    return;
                }
                URL url = this.getClass().getResource("/demo/" + messageMeta.getActionId() + ".properties");
                if (StringUtils.isNotEmpty(resourceFolder)) {
                    url = new URL("file://" + resourceFolder + "/" + messageMeta.getActionId() + ".properties");
                }
                final URL finalUrl = url;
                Props actionProps = propertyCache.get(messageMeta.getActionId(), () -> new Props(finalUrl.getFile()));
                actionIdCache.put(messageMeta.getActionId(), messageMeta);
                ConsumerJobContext ctx = ConsumerJobContext.builder()
                        .messageMeta(messageMeta)
                        .message(message)
                        .propertyFile(actionProps)
                        .build();
                String bean = actionProps.getStr(PropertyConstant.BATCH_JOB_HANDLER_NAME);
                RecordHandler handler = appCtx.getBean(bean, RecordHandler.class);
                BatchJobReport report = handler.processRecord(ctx);
                if (JobStatus.COMPLETED == jobStatus) {
                    log.info("=======report======= {}", report);
                }
                actionIdCache.invalidate(messageMeta.getActionId());
            } catch (Exception e) {
                log.error("error while covert to class", e);
            }
        }

    }

    private boolean ignoreMessageIfNeeded(MessageHeader messageHeader) {
        String key = KEY_PREFIX + messageHeader.getRequestToken();
        String exceptionTimeStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(exceptionTimeStr)) {
            return false;
        }
        return messageHeader.getTimestamp() > Long.valueOf(exceptionTimeStr);
    }

    @Override
    public void destroy() throws Exception {
        //当应用异常结束时候，需要知道如何处理接下来的消息
        Map<String, MessageHeader> map = actionIdCache.asMap();
        Duration duration = calDurationBetweenTodayStartAndEnd();
        log.info("---------handle shutdown exception--------");
        for (Map.Entry<String, MessageHeader> entry : map.entrySet()) {
            MessageHeader messageHeader = entry.getValue();
            String key = KEY_PREFIX + messageHeader.getRequestToken();
            //记录异常发生的时间
            redisTemplate.opsForValue().set(key, String.valueOf(messageHeader.getTimestamp()));
            //每天0点失效
            redisTemplate.expire(key, duration);
        }
    }

    private Duration calDurationBetweenTodayStartAndEnd() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return Duration.between(currentTime, todayEnd);
    }
}