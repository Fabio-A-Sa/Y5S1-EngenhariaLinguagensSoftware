package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling operation-related errors.
 */
public class OperationException extends Exception {

    /**
     * Constructs a new OperationException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public OperationException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "Operation Error:".
     */
    @Override
    public String getMessage() {
        return "Operation Error: " + super.getMessage();
    }
}
