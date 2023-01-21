package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import models.ApiError;
import models.ApiValidationError;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Slf4j
public class Generic {
  public static String getResponseMessage(Response response, @Nullable String body) {
    if (Objects.isNull(body)) {
      return "Can't get response message";
    }
    if (response.code() == 422) {
      // TODO: Improve error message for 422 responses
      var message = getResponseMessageFromValidationError(response, body).getErrors().get(0).getMsg();
      // TODO: Create map of error messages to bot messages
      if (message.equals("string does not match regex \"(\\d{4}\\.(0[1-9]|[1-4][0-9]|5[0-4])|next|current|last)$\"")) {
        return "Identificador de semana inválido (formato: AAAA.SS, ejemplo: 2023.01)";
      }
      return message;
    }
    return getResponseMessageFromGenericError(response, body).getDetail();
  }

  private static ApiError getResponseMessageFromGenericError(Response response, String body) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(body, ApiError.class);
    } catch (JsonProcessingException e) {
      log.error("Error parsing response message", e);
      throw new RuntimeException(e);
    }
  }

  private static ApiValidationError getResponseMessageFromValidationError(Response response, String body) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(body, ApiValidationError.class);
    } catch (JsonProcessingException e) {
      log.error("Error parsing response message", e);
      throw new RuntimeException(e);
    }
  }
}
