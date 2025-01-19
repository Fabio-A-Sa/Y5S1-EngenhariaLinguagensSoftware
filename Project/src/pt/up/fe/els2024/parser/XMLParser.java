package pt.up.fe.els2024.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pt.up.fe.els2024.exception.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * {@code XMLParser} is a subclass of {@link FileParser} responsible for parsing XML files.
 * <p>
 * This class uses the Jackson library's {@link XmlMapper} to read and convert XML data into a {@link Map}.
 * It provides the implementation for the {@link FileParser#readData(File)} method specific to XML files.
 * </p>
 */
public class XMLParser extends FileParser {

    /**
     * The Jackson {@link ObjectMapper} instance configured to read XML files.
     * It is used to deserialize XML content into a {@link Map} structure.
     */
    private final ObjectMapper xmlMapper = new XmlMapper();

    /**
     * Reads data from the provided XML file and converts it into a {@link Map}.
     * <p>
     * This method uses the Jackson {@link XmlMapper} to read the XML file and convert its contents
     * into a {@link Map}. The resulting map represents the hierarchical structure of the XML data.
     * </p>
     *
     * @param file the XML file to read
     * @return a {@link Map} representing the parsed XML content
     * @throws IOException if an error occurs during reading the file
     * @throws ParserException if the XML file cannot be parsed correctly
     */
    @Override
    public Map<String, Object> readData(File file) throws IOException, ParserException {
        // Use the xmlMapper to read and convert XML file to a Map
        return xmlMapper.readValue(file, Map.class);
    }
}
