package exceptions;

/*
 * Custom exception class for no cookies sent in the requet
 */
public class NoCookiesExceptions extends Exception {
	
	public NoCookiesExceptions() {
		super();
	}
	
	public NoCookiesExceptions(String msg) {
		super(msg);
	}
	
}
