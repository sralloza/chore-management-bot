package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ChoreType {
  private String id;
  private String name;
  private String description;
}
