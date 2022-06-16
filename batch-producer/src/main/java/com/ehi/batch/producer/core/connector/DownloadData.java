package com.ehi.batch.producer.core.connector;

import lombok.Builder;
import lombok.Data;

/**
 * @author portz
 * @date 06/13/2022 16:41
 */
@Data
@Builder
public class DownloadData {
    private String data;
    private Integer currentBatch;
    private String actionId;
}
