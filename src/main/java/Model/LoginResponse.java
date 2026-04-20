package Model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "Login response payload")
public class LoginResponse {

    @Schema(description = "CSRF token for subsequent protected requests", example = "550e8400-e29b-41d4-a716-446655440000")
    public String csrfToken;

    @Schema(description = "Username of the authenticated user", example = "team1")
    public String username;

    @Schema(description = "JWT/session token if login is successful", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    public String token;

    @Schema(description = "Display name of the authenticated user", example = "Team 1 - AAST")
    public String displayName;

    public LoginResponse() {}

    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public LoginResponse(String username, String token, String displayName) {
        this.username = username;
        this.token = token;
        this.displayName = displayName;
    }
}