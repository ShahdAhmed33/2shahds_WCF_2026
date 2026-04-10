package exceptions;

/**
 * Custom exception thrown when a cookie is present but its signature or 
 * integrity check fails (e.g., the JWT has been tampered with).
 */
public class NotVerifiedCookieException extends Exception {

    // Default constructor
    public NotVerifiedCookieException() {
        super();
    }

    // Constructor that accepts a custom error message
    public NotVerifiedCookieException(String message) {
        super(message);
    }

    // Constructor for wrapping another exception (cause)
    public NotVerifiedCookieException(String message, Throwable cause) {
        super(message, cause);
    }
}