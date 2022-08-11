package models;

import lombok.Data;

import java.util.Map;

@Data
public class Ticket {
    private String description;
    private String id;
    private Map<String, Integer> ticketsByTenant;
}
