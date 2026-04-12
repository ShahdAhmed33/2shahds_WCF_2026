package exceptions;

/*
 * Custom exception for No HTTP request
 */
public class NoHttpRequestExcepion extends Exception {
	
	public NoHttpRequestExcepion() {
		super();
	}
	
	public NoHttpRequestExcepion(String msg) {
		super(msg);
	}
	
}
