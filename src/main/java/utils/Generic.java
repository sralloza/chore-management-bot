package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import models.ApiError;
import models.ApiValidationError;

import java.net.http.HttpResponse;

@Slf4j
public class Generic {
  public static String getResponseMessage(HttpResponse<String> response) {
    if (response.statusCode() == 422) {
      // TODO: Improve error message for 422 responses
      var message = getResponseMessageFromValidationError(response).getErrors().get(0).getMsg();
      if (message.equals("string does not match regex \"(\\d{4}\\.(0[1-9]|[1-4][0-9]|5[0-4])|next|current|last)$\"")) {
        return "Identificador de semana inv\u00E1lido (formato: AAAA.SS, ejemplo: 2023.01)";
      }
      return message;
    }
    return getResponseMessageFromGenericError(response).getDetail();
  }

  private static ApiError getResponseMessageFromGenericError(HttpResponse<String> response) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(response.body(), ApiError.class);
    } catch (JsonProcessingException e) {
      log.error("Error parsing response message", e);
      throw new RuntimeException(e);
    }
  }

  private static ApiValidationError getResponseMessageFromValidationError(HttpResponse<String> response) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(response.body(), ApiValidationError.class);
    } catch (JsonProcessingException e) {
      log.error("Error parsing response message", e);
      throw new RuntimeException(e);
    }
  }
}
