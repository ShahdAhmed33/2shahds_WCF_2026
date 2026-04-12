package models;



public class LoginResponseModel {
	public String CookeiID;
	public String Username;
	
	public LoginResponseModel() {
		
	}
	
	public LoginResponseModel(String CookeiID, String Username) {
		this.CookeiID = CookeiID;
		this.Username = Username;
	}
	
	public String getCookieID() { return this.CookeiID; }
	
	public String getUsername() { return this.Username; }

}
