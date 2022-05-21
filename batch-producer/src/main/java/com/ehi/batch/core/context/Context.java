package com.ehi.batch.core.context;

import cn.hutool.setting.dialect.Props;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author portz
 * @date 05/16/2022 20:31
 */
@Data
@SuperBuilder(toBuilder = true)
public class Context {
    private String actionId;
    private String requestToken;
    private Props actionProps;
}
