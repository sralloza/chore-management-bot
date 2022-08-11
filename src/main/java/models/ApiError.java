package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class ApiError {
    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
