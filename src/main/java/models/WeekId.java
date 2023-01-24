package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class WeekId {
  @JsonProperty("week_id")
  private String weekId;
}
