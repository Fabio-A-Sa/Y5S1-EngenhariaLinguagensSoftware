package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling export-related errors.
 */
public class ExportException extends Exception {

    /**
     * Constructs a new ExportException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public ExportException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "Export Error:".
     */
    @Override
    public String getMessage() {
        return "Export Error: " + super.getMessage();
    }
}
