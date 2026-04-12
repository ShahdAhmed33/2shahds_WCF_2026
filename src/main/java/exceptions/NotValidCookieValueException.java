package exceptions;

/*
 * custom exception if the cookie id value is not found in the hash map
 */
public class NotValidCookieValueException extends Exception {
	
	public NotValidCookieValueException() {
		super();
	}
	
	public NotValidCookieValueException(String msg) {
		super(msg);
	}
}
