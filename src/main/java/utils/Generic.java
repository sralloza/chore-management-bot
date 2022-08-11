package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import models.ApiError;

import java.net.http.HttpResponse;

@Slf4j
public class Generic {
    public static ApiError getResponseMessage(HttpResponse<String> response) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(response.body(), ApiError.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing response message", e);
            throw new RuntimeException(e);
        }
    }
}
