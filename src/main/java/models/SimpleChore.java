package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SimpleChore {
    @JsonProperty("chore_type")
    private String choreType;
    @JsonProperty("tenant_id")
    private Long tenantId;
    @JsonProperty("week_id")
    private String weekId;
    private Boolean done;
}
