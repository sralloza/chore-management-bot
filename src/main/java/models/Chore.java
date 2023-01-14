package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Chore {
  @JsonProperty("chore_type_id")
  private String choreTypeId;
  @JsonProperty("user_id")
  private Long userId;
  @JsonProperty("week_id")
  private String weekId;
  private Boolean done;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("completed_at")
  private LocalDateTime completedAt;
}
