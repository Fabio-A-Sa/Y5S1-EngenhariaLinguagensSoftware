package pt.up.fe.els2024.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * {@code YAMLParser} is a subclass of {@link FileParser} responsible for parsing YAML files.
 * <p>
 * This class uses the Jackson library's {@link ObjectMapper} configured with {@link YAMLFactory}
 * to read and convert YAML data into a {@link Map}.
 * </p>
 */
public class YAMLParser extends FileParser {

    /**
     * The Jackson {@link ObjectMapper} instance configured with {@link YAMLFactory} to parse YAML files.
     * It is used to deserialize YAML content into a {@link Map}.
     */
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Reads data from the provided YAML file and converts it into a {@link Map}.
     * <p>
     * This method uses the Jackson {@link ObjectMapper} with {@link YAMLFactory} to read the YAML file
     * and convert its contents into a {@link Map}. The resulting map represents the hierarchical structure
     * of the YAML data.
     * </p>
     *
     * @param file the YAML file to read
     * @return a {@link Map} representing the parsed YAML content
     * @throws IOException if an error occurs during reading the file
     */
    @Override
    public Map<String, Object> readData(File file) throws IOException {
        // Use the yamlMapper to read and convert YAML file to a Map
        return yamlMapper.readValue(file, Map.class);
    }
}
