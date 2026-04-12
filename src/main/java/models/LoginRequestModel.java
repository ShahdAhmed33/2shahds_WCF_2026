package models;

//Class represents the LoginRequestModel to be mapped to JSONs
public class LoginRequestModel {
	public String username;
	public String password;
	
	public LoginRequestModel() {}
	
	public LoginRequestModel(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() { return this.username; }
	
	public String getPassword() { return this.password; }
}

