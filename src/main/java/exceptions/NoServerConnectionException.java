package exceptions;

/*
 * Custom exception that the ServerConnection is not in the hashmap (EXpired)
 */
public class NoServerConnectionException extends Exception {
	
	public NoServerConnectionException() {
		super();
	}
	
	public NoServerConnectionException(String msg) {
		super(msg);
	}
	

}
