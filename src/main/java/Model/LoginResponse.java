package Model;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "LoginResponse", description = "Login response payload")

public class LoginResponse {
    @Schema(description = "Status of the login attempt", example = "SUCCESS")
	public String username;
    
   @Schema(description = "JWT token if login is successful", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
   public String token;
	
	public LoginResponse() {}
	
	public LoginResponse(String username, String token) {
		
		this.username=username;
		this.token=token;
	}
}
