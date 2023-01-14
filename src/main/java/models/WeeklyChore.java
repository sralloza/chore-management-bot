package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeeklyChore {
    @JsonProperty("assigned_ids")
    private List<String> assignedIds;
    @JsonProperty("assigned_usernames")
    private List<String> assignedUsernames;
    private Boolean done;
    @JsonProperty("chore_type_id")
    private String choreTypeId;
    @JsonProperty("week_id")
    private String weekId;
}
