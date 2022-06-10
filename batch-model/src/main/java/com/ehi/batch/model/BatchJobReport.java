package com.ehi.batch.model;

import lombok.Data;

/**
 * @author portz
 * @date 06/09/2022 11:00
 */
@Data
public class BatchJobReport {
    private String actionId;
    private String requestToken;
    private BatchJobMetric metrics;
}
