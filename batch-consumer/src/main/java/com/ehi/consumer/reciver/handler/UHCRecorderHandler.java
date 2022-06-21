package com.ehi.consumer.reciver.handler;


import com.ehi.batch.model.UhcDataObject;
import com.ehi.consumer.WritetoNFS;
import com.ehi.consumer.reciver.ConsumerJobContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author portz
 * @date 05/21/2022 11:03
 */
@Component
@Slf4j
public class UHCRecorderHandler extends AbstractRecordHandler {
    public static final String ACTION_ID = "uhc-data-exchange";
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WritetoNFS writetoNFS;

    private Gson gson = new GsonBuilder().create();

    @PostConstruct
    public void initProps() {

    }

    @Override
    public void whenJobComplete(ConsumerJobContext ctx) {
        log.info("UHC write NFS");
        writetoNFS.writetoNFS();
    }

    @Override
    public void handleEachRecord(ConsumerJobContext ctx) {
        String actionId = ctx.getMessageMeta().getActionId();
        String objectMapper = ctx.getMessageMeta().getObjectModel();
        try {
            Class clzz = Class.forName(objectMapper);
            UhcDataObject message = (UhcDataObject) gson.fromJson(ctx.getMessage().toString(), clzz);
            redisTemplate.opsForSet().add(actionId, ctx.getMessage().toString());
        } catch (Exception e) {
            log.error("covert to object error", e);
        }
    }
}
