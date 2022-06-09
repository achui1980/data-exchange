package com.ehi.consumer.reciver;

import cn.hutool.setting.dialect.Props;
import com.ehi.batch.PropertyConstant;
import com.ehi.batch.model.BatchJobReport;
import com.ehi.batch.model.MessageHeader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Optional;

/**
 * @author portz
 * @date 04/24/2022 20:59
 */
@Component
@Slf4j
public class KafkaReceiver {

    @Autowired
    ApplicationContext appCtx;

    Gson gson;
    private Cache<String, Props> cache;

    @PostConstruct
    public void register() {
        gson = new GsonBuilder().create();
        cache = CacheBuilder.newBuilder().build();

    }

    @KafkaListener(topics = {"${spring.kafka.topic}"})
    public void listen(ConsumerRecord<?, ?> record, @Header("X-Batch-Meta-Json") String metaJson) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                MessageHeader messageMeta = gson.fromJson(metaJson, MessageHeader.class);
                boolean isJobComplete = messageMeta.isJobComplete();
                URL url = this.getClass().getResource("/demo/" + messageMeta.getActionId() + ".properties");
                Props actionProps = cache.get(messageMeta.getActionId(), () -> new Props(url.getFile()));
                ConsumerJobContext ctx = ConsumerJobContext.builder()
                        .messageMeta(messageMeta)
                        .message(message)
                        .propertyFile(actionProps)
                        .build();
                String bean = actionProps.getStr(PropertyConstant.BATCH_JOB_HANDLER_NAME);
                RecordHandler handler = appCtx.getBean(bean, RecordHandler.class);
                BatchJobReport report = handler.processRecord(ctx);
                if (isJobComplete) {
                    log.info("=======report======= {}", report);
                }
            } catch (Exception e) {
                log.error("error while covert to class", e);
            }
        }

    }
}