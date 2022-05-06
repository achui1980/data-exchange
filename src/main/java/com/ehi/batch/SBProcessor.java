package com.ehi.batch;

import org.springframework.batch.item.ItemProcessor;

/**
 * @author portz
 * @date 04/24/2022 16:20
 */
public class SBProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String data) throws Exception {
        return data.toUpperCase();
    }
}
