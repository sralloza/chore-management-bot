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
    private Integer statusCode;

    public APIException(HttpResponse<String> response) {
        super(Generic.getResponseMessage(response).getMessage());
        this.msg = Generic.getResponseMessage(response).getMessage();
        this.url = response.uri().toString();
        this.statusCode = response.statusCode();
    }
}
