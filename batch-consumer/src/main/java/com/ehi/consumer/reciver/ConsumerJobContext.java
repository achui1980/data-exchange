package com.ehi.consumer.reciver;

import cn.hutool.setting.dialect.Props;
import com.ehi.batch.model.MessageHeader;
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
    private Props propertyFile;
}