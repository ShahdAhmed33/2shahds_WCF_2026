package exceptions;

/*
 * Custom exception that the user is not logged in ( may be lost connection to server)
 */
public class NotLoggedIn extends Exception {
	
	public NotLoggedIn() {
		super();
	}
	
	public NotLoggedIn(String msg) {
		super(msg);
	}

}
