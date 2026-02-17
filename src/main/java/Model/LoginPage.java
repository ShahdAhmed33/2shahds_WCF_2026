package Model;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "LoginPage", description = "Login request payload")
public class LoginPage {
    @Schema(description = "Username of the user", example = "admin")
	public String username;
    
    @Schema(description = "Password of the user", example = "123456")
	public String password;
    
	public LoginPage() {}
	public LoginPage(String username,String password) {
		this.username=username;
		this.password=password;
	}
}
