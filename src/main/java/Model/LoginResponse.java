package Model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Schema(name = "LoginResponse", description = "Login response payload")
@JsonPropertyOrder({ "csrfToken", "username", "token", "displayName" })
public class LoginResponse {
    
    @JsonProperty("csrfToken")
    public String csrfToken;
    
    @JsonProperty("username")
    public String username;
    
    @JsonProperty("token")
    public String token;
    
    @JsonProperty("displayName") // This forces the name in JSON
    public String displayName;
    
    public LoginResponse() {}
    
    public LoginResponse(String username, String token, String displayName, String csrfToken) {
        this.username = username;
        this.token = token;
        this.displayName = displayName;
        this.csrfToken = csrfToken;
    }
}