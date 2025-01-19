package pt.up.fe.els2024.exception;

import java.lang.Exception;

/**
 * Custom exception class for handling errors during parsing operations.
 */
public class ParserException extends Exception {

    /**
     * Constructs a new ParserException with the specified error message.
     * 
     * @param message The detail message explaining the error.
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Overrides the getMessage method to provide a custom error message.
     * 
     * @return A detailed error message prefixed with "Parsing Error:".
     */
    @Override
    public String getMessage() {
        return "Parsing Error: " + super.getMessage();
    }
}
