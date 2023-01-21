package exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import utils.Generic;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIException extends RuntimeException {
  private String msg;
  private String url;
  private String method;
  private Integer statusCode;
  private String xCorrelator;
  private String apiKey;
  private Exception otherException;

  public APIException(String responseMessage) {
    super(responseMessage);
  }

  public static APIException from(Exception exc) {
    var apiException = new APIException();
    apiException.setOtherException(exc);
    return apiException;
  }

  public static APIException from(Response response, String body) {
    return APIException.from(response, body, null);
  }

  public static APIException from(Response response, String body, @Nullable Exception exc) {
    String apiErrorMsg = Objects.isNull(exc) ? Generic.getResponseMessage(response, body) : null;
    APIException apiException;
    if (Objects.nonNull(apiErrorMsg)) {
      apiException = new APIException(apiErrorMsg);
    } else {
      apiException = new APIException(exc.getMessage());
    }
    apiException.msg = apiErrorMsg;
    apiException.url = response.request().url().toString();
    apiException.method = response.request().method();
    apiException.statusCode = response.code();
    apiException.xCorrelator = response.headers().get("X-Correlator");
    apiException.apiKey = response.request().headers().get("x-token");
    apiException.setOtherException(exc);
    return apiException;
  }
}
