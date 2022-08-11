package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SimpleChore {
    private String choreType;
    private Integer tenantId;
    private String weekId;
    private Boolean done;
}
