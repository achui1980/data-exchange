package com.ehi.batch.domain;

import com.ehi.batch.core.exception.BatchJobException;
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
    private String mapperClass;
    private String requestToken;
    private boolean jobComplete;
    private boolean jobStart;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
           return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new BatchJobException("covert to json error");
        }
    }
}
