package com.ehi.batch.consumer;

import com.ehi.batch.WritetoNFS;
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
public class DefaultRecorderHandler extends AbstractRecordHandler {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WritetoNFS writetoNFS;

    @Override
    public void whenJobComplete(ConsumerJobContext ctx) {
        writetoNFS.writetoNFS();
    }

    @Override
    public void handleEachRecord(ConsumerJobContext ctx) {
        String actionId = ctx.getMessageMeta().getActionId();
        redisTemplate.opsForSet().add(actionId, ctx.getMessage().toString());
    }
}
