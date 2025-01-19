package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling import-related errors.
 */
public class ImportException extends Exception {

    /**
     * Constructs a new ImportException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "Import Error:".
     */
    @Override
    public String getMessage() {
        return "Import Error: " + super.getMessage();
    }
}
