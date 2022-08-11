package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeeklyChores {
    private List<Chore> chores;
    private Integer rotation;
    @JsonProperty("week_id")
    private String weekId;
}

