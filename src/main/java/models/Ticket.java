package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Ticket {
    private String description;
    private String id;
    @JsonProperty("tickets_by_tenant")
    private Map<String, Integer> ticketsByTenant;
}
