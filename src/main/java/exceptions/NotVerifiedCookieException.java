package exceptions;

/*
 * cutom exception in case the cookie value does not bas verification (Not signed)
 */
public class NotVerifiedCookieException  extends Exception {
	
	public NotVerifiedCookieException() {
		super();
	}
	
	
	public NotVerifiedCookieException(String msg) {
		super(msg);
	}

}
