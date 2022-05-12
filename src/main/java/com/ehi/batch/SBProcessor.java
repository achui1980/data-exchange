package com.ehi.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 04/24/2022 16:20
 */
@Component("SBProcessor")
public class SBProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String data) throws Exception {
        return data.toUpperCase();
    }
}