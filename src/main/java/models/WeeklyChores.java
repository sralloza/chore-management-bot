package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class WeeklyChores {
  private List<WeeklyChore> chores;
  private Integer rotation;
  @JsonProperty("week_id")
  private String weekId;
}

