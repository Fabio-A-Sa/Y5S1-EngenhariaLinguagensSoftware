package pt.up.fe.els2024.parser;

import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ParserException;
import pt.up.fe.els2024.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * {@code Parser} is a utility class responsible for parsing files based on their extension.
 * It determines the file type (e.g., JSON, YAML, XML) and delegates the parsing task to the corresponding
 * parser class that handles that specific file type.
 * <p>
 * The primary method in this class is {@link #parseFile(File)}, which identifies the file extension
 * and uses the appropriate parser to process the file and convert its content into a {@link Table}.
 * </p>
 */
public class Parser {

    /**
     * Constructs a new {@code Parser} instance.
     * This constructor doesn't require any arguments as no initialization is necessary.
     */
    public Parser() {}

    /**
     * Parses the given file and converts it into a {@link Table} object.
     * <p>
     * The method determines the file type by its extension, and uses the appropriate {@link FileParser}
     * to parse the file. Supported file types are JSON, YAML, and XML. If the file extension is not recognized,
     * a {@link ParserException} is thrown.
     * </p>
     *
     * @param file the file to parse
     * @return a {@link Table} object that represents the parsed content of the file
     * @throws IOException if an error occurs while reading the file
     * @throws ParserException if the file's extension is unsupported or the parsing fails
     */
    public Table parseFile(File file) throws IOException, ParserException {
        // Get the file extension using the utility method
        String extension = Utils.getFileExtension(file);
        
        // Get the appropriate parser for the file based on its extension
        FileParser fileParser = getFileParser(extension);
        
        // Use the chosen file parser to parse the file and return the resulting Table
        return fileParser.parseFile(file, file.getName());
    }

    /**
     * Returns the appropriate {@link FileParser} instance based on the provided file extension.
     * <p>
     * This method supports the following file extensions:
     * <ul>
     *   <li>"json" - for JSON files</li>
     *   <li>"yaml" - for YAML files</li>
     *   <li>"xml" - for XML files</li>
     * </ul>
     * If the file extension is not supported, a {@link ParserException} is thrown.
     * </p>
     *
     * @param extension the file extension (e.g., "json", "yaml", "xml")
     * @return the corresponding {@link FileParser} instance
     * @throws ParserException if the extension is not recognized
     */
    private FileParser getFileParser(String extension) throws ParserException {
        
        // Return the appropriate file parser based on the extension
        return switch (extension.toLowerCase()) {
            case "json" -> new JSONParser();
            case "yaml" -> new YAMLParser();
            case "xml" -> new XMLParser();
            default -> throw new ParserException("Unknown input extension: " + extension);
        };
    }
}
