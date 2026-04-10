package exceptions;

public class ProblemNotFoundException extends Exception {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}