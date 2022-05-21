package com.ehi.batch.core.context;

import lombok.Builder;
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
