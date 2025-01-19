package pt.up.fe.els2024.builders;

/**
 * An abstract base class for constructing and finalizing an object or process. 
 * This class provides a standard `end` method for completing the build process.
 * The {@code name} field can be used to store a name associated with the builder.
 */
public abstract class Builder {

    /**
     * A static variable to store the name associated with the builder.
     */
    static String name;

    /**
     * Finalizes the building process and returns the current instance of the builder.
     *
     * @return the current instance of {@code Builder}.
     * @throws Exception 
    */
    public Builder end() throws Exception {
        throw new Exception(".end() method not implemented!");
    }

    /**
     * Finalizes the endWhen building process and returns the current instance of the builder.
     *
     * @return the current instance of {@code Builder}.
     * @throws Exception 
    */
    public Builder endWhen() throws Exception {
        throw new Exception(".endWhen() method not implemented!");
    }
}
