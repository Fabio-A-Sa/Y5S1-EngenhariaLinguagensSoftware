package pt.up.fe.els2024.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.up.fe.els2024.exception.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * {@code JSONParser} is a subclass of {@link FileParser} that provides functionality for parsing data from JSON files.
 * This parser uses the Jackson {@link ObjectMapper} to read and convert JSON data into a {@link Map} of key-value pairs,
 * which can then be processed into a Table object.
 * 
 * <p>It implements the {@link FileParser#readData(File)} method to handle JSON-specific file reading and parsing.</p>
 */
public class JSONParser extends FileParser {

    // Jackson ObjectMapper for parsing JSON
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Reads the data from a JSON file and returns it as a map of key-value pairs.
     * 
     * <p>This method uses the Jackson {@link ObjectMapper} to deserialize the contents of the file into a Java {@link Map}.
     * The resulting map represents the structure of the JSON data, where each key corresponds to a JSON field and each
     * value corresponds to the associated data.</p>
     * 
     * @param file the JSON file to read
     * @return a map representing the data in the JSON file
     * @throws IOException if there is an issue reading or parsing the file
     * @throws ParserException if the file cannot be parsed into the expected structure
     */
    @Override
    public Map<String, Object> readData(File file) throws IOException, ParserException {
        try {
            // Use Jackson ObjectMapper to read the file and convert it to a Map
            return this.jsonMapper.readValue(file, Map.class);
        } catch (IOException e) {
            // Throw a custom ParserException if the JSON is malformed or unreadable
            throw new ParserException("Error reading or parsing JSON file: " + file.getName());
        }
    }
}
