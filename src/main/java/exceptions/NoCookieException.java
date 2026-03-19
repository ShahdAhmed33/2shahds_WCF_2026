package exceptions;

/**
 * Custom exception thrown when a required cookie is missing from the HTTP request.
 */
public class NoCookieException extends Exception {

    // Default constructor
    public NoCookieException() {
        super();
    }

    // Constructor that accepts a custom error message
    public NoCookieException(String message) {
        super(message);
    }

    // Constructor for wrapping another exception (cause)
    public NoCookieException(String message, Throwable cause) {
        super(message, cause);
    }
}