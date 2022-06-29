package com.ehi.batch.model;

import com.ehi.batch.JobStatus;
import com.ehi.batch.exception.BatchJobException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author portz
 * @date 05/18/2022 18:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageHeader {
    private String actionId;
    private Long rowNumber;
    private String objectModel;
    private String requestToken;
    private Long timestamp = System.currentTimeMillis();
    private JobStatus jobStatus = JobStatus.PROCESSING;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new BatchJobException("covert to json error");
        }
    }
}
