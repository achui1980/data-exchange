package com.ehi.batch.model;

import lombok.Data;

/**
 * @author portz
 * @date 06/09/2022 10:57
 */
@Data
public class BatchJobMetric {
    private String processTime;
    private Long duration;
    private Integer totalCount;
    private Integer successCount;
    private Integer failureCount;
}
