package com.ehi.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author portz
 * @date 04/27/2022 11:23
 */
@Component
@Slf4j
public class WritetoNFS {

    @Autowired
    RedisTemplate<String, String> template;

    public void writetoNFS() {
        Set<String> set = template.opsForSet().members("actionId");
        List<String> list = template.opsForSet().pop("actionId", 3);
        while (!CollectionUtils.isEmpty(list)) {
            for (String msg : set) {
                log.info(msg);
            }
            list = template.opsForSet().pop("actionId", 3);
        }

    }
}
