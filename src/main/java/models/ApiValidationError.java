package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ApiValidationError {
  private List<ApiSingleValidationError> errors;
}
