package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Chore {
    @JsonProperty("assigned_ids")
    private List<Long> assignedIds;
    @JsonProperty("assigned_usernames")
    private List<String> assignedUsernames;
    private Boolean done;
    private String type;
    @JsonProperty("week_id")
    private String weekId;
}
