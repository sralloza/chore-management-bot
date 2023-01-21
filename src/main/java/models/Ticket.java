package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
@Data
public class Ticket {
  private String description;
  private String name;
  private String id;
  @JsonProperty("tickets_by_user_id")
  private Map<String, Integer> ticketsByUserId;
  @JsonProperty("tickets_by_user_name")
  private Map<String, Integer> ticketsByUserName;
}
