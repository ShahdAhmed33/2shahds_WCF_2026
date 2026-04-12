package testModels;

public class TestLoginRequestModel {
	private String username;
	private String password;
	
	public TestLoginRequestModel(String username, String password) {
		this.setPassword(password);
		this.setUsername(username);
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
