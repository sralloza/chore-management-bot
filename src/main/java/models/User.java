package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private String username;
    @JsonProperty("id")
    private String id;
    @JsonProperty("api_key")
    private String apiKey;
}
