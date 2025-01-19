package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling DSL-related errors.
 */
public class DSLException extends Exception {

    /**
     * Constructs a new DSLException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public DSLException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "DSL Error:".
     */
    @Override
    public String getMessage() {
        return "DSL Error: " + super.getMessage();
    }
}
