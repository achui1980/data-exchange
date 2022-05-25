package com.ehi.batch.producer.core.context;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author portz
 * @date 05/16/2022 16:23
 */
@Data
@SuperBuilder(toBuilder = true)
public class FetchContext extends Context{
    private String propertyFile;
}
