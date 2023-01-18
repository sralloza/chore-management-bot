package exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import utils.Generic;

import java.net.http.HttpResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class APIException extends RuntimeException {
  private String msg;
  private String url;
  private String method;
  private Integer statusCode;
  private String xCorrelator;
  private String apiKey;
  private Exception otherException;

  public APIException(HttpResponse<String> response, Exception exc) {
    this(response);
    this.otherException = exc;
  }

  public APIException(HttpResponse<String> response) {
    super(Generic.getResponseMessage(response));
    this.msg = Generic.getResponseMessage(response);
    this.url = response.uri().toString();
    this.method = response.request().method();
    this.statusCode = response.statusCode();
    this.xCorrelator = response.headers().firstValue("X-Correlator").orElse(null);
    this.apiKey = response.request().headers().firstValue("x-token").orElse(null);
  }
}
