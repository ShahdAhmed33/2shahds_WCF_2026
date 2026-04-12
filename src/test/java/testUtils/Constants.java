package testUtils;

public class Constants {
	 private Constants() {
		 
	        throw new AssertionError(); // Optional: defense against reflection instantiation
	    }

	    // Declare constants
	    public static final String SERVER_URL = "http://localhost:8080";
	    public static final String LOGIN_URL = "/api/contest/login";
	  
}
