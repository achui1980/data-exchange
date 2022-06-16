package com.ehi.batch.producer.example.connector;

import com.ehi.batch.exception.BatchJobException;
import com.ehi.batch.producer.core.connector.restful.MultipleRestfulConnector;
import com.ehi.batch.producer.core.connector.restful.RestfulConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 06/13/2022 14:05
 */
@Component("HumanaConnector")
@Slf4j
public class HumanaConnector extends MultipleRestfulConnector {

    @Override
    public String getRequestBody() {
        return "{\n" +
                "  \"partnerId\": \"1273481\",\n" +
                "  \"coverageStartDate\": \"2022-04-01\",\n" +
                "  \"coverageEndDate\": \"2022-04-30\",\n" +
                "  \"sort\": {\n" +
                "    \"by\": \"lastStatusUpdateDate\",\n" +
                "    \"order\": \"asc\"\n" +
                "  },\n" +
                "  \"offset\":0,\n" +
                "  \"limit\": 100\n" +
                "}";
    }

    @Override
    public String queryParams() {
        return StringUtils.EMPTY;
    }

    @Override
    public String handlerJsonResponse(String jsonResponse) throws BatchJobException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonResponse).get("records").toString();
        } catch (JsonProcessingException e) {
            throw new BatchJobException("convert to json error", e);
        }
    }
}
