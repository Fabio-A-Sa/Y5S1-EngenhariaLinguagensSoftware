package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling select-related errors.
 */
public class SelectException extends Exception {

    /**
     * Constructs a new SelectException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public SelectException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "Select Error:".
     */
    @Override
    public String getMessage() {
        return "Import Error: " + super.getMessage();
    }
}
