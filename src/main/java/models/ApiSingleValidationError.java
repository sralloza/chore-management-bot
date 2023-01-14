package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(value = { "ctx" })
public class ApiSingleValidationError {
  private List<String> loc;
  private String msg;
  private String type;
}
