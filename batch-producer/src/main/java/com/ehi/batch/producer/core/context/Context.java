package com.ehi.batch.producer.core.context;

import cn.hutool.setting.dialect.Props;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author portz
 * @date 05/16/2022 20:31
 */
@Data
@SuperBuilder(toBuilder = true)
public class Context implements Serializable {
    private String actionId;
    private String requestToken;
    private Props actionProps;
    private String batch;
}
