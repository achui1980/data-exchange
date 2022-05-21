package com.ehi.batch.consumer;

import com.ehi.batch.domain.MessageHeader;
import lombok.Builder;
import lombok.Data;

/**
 * @author portz
 * @date 05/18/2022 20:50
 */
@Data
@Builder
public class ConsumerJobContext {
    private MessageHeader messageMeta;
    private Object message;
    private String propertyFile;
}
