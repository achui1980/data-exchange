package com.ehi.batch;

import com.ehi.batch.kafka.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author portz
 * @date 04/24/2022 16:21
 */
@Slf4j
@Component
public class SBWriter implements ItemWriter<String> {

    @Autowired
    KafkaSender sender;

    @Override
    public void write(List<? extends String> messages) throws Exception {
        for (String msg : messages) {
            log.info("Writing the data \n" + msg);
            sender.send(msg);
        }
    }
}
