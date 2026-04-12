package exceptions;

/*
 * Custom exception, in case there is cookie without the awt-jwt ( the cookie id name)
 */
public class NoCookieException extends Exception {
					
	public NoCookieException() {
		super();
	}
	
	public NoCookieException(String msg) {
		super(msg);
	}
	

}
