package com.ehi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
        //int totalCount = template.opsForSet().members("uhc-data-exchange").size();
        List<String> list = template.opsForSet().pop("uhc-data-exchange", 3);
        while (!CollectionUtils.isEmpty(list)) {
            list = template.opsForSet().pop("uhc-data-exchange", 3);
        }
    }
}