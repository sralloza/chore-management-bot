package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Ticket {
    private String description;
    private String name;
    private String id;
    @JsonProperty("tickets_by_user_id")
    private Map<String, String> ticketsByUserId;
    @JsonProperty("tickets_by_user_name")
    private Map<String, String> ticketsByUserName;
}
