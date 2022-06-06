package com.ehi.consumer.reciver;


import com.ehi.batch.model.GTLDataObject;
import com.ehi.consumer.WritetoNFS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 05/21/2022 11:03
 */
@Component
@Slf4j
public class GTLRecorderHandler extends AbstractRecordHandler {

    public static final String ACTION_ID = "gtl-data-exchange";
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WritetoNFS writetoNFS;

    private Gson gson = new GsonBuilder().create();

    @Override
    public void whenJobComplete(ConsumerJobContext ctx) {
        log.info("GTL write NFS");
        writetoNFS.writetoNFS();
    }

    @Override
    public void handleEachRecord(ConsumerJobContext ctx) {
        String actionId = ctx.getMessageMeta().getActionId();
        try {
            GTLDataObject message = gson.fromJson(ctx.getMessage().toString(), GTLDataObject.class);
            redisTemplate.opsForSet().add(actionId, ctx.getMessage().toString());
        } catch (Exception e) {
            log.error("covert to object error", e);
        }
    }
}
