package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Tenant {
    private String username;
    @JsonProperty("id")
    private String id;
    @JsonProperty("api_token")
    private String apiToken;
}
