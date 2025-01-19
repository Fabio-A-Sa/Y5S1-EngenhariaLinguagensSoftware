package pt.up.fe.els2024.parser;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class {@code FileParser} provides functionality for parsing data from a file into a {@link Table} object.
 * Subclasses must implement the {@link #readData(File)} method to handle the file-specific parsing logic.
 * The class is designed to work with files that contain structured data, which can be parsed into columns and rows.
 * 
 * <p>This class supports both simple values and nested data structures, such as lists and maps, and provides methods
 * to convert them into a structured {@link Table} representation.</p>
 */
public abstract class FileParser {

    /**
     * Reads the raw data from a file and returns it as a map of key-value pairs.
     * 
     * <p>This method must be implemented by subclasses to provide the specific file-reading logic.</p>
     * 
     * @param file the file to read the data from
     * @return a map representing the data in the file
     * @throws IOException if there is an issue reading the file
     * @throws ParserException if there is a problem parsing the file's contents
     */
    public abstract Map<String, Object> readData(File file) throws IOException, ParserException;

    /**
     * Parses the data from a file and returns it as a {@link Table}.
     * 
     * <p>This method first calls {@link #readData(File)} to obtain the raw data from the file and then
     * processes it using the {@link #parse(Map, String)} method to convert it into a structured table.</p>
     * 
     * @param file the file to parse
     * @param fileName the name of the file, used for the table's identification
     * @return a {@link Table} object representing the parsed data
     * @throws IOException if there is an issue reading the file
     * @throws ParserException if there is a problem parsing the file's contents
     */
    public Table parseFile(File file, String fileName) throws IOException, ParserException {
        Map<String, Object> data = readData(file);
        return parse(data, fileName);
    }

    /**
     * Extracts key-value pairs from an object, assuming it is a {@link LinkedHashMap}.
     * 
     * <p>This method converts the raw data into a {@link LinkedHashMap} with {@link String} keys and {@link Object} values.
     * It is useful when parsing nested data structures.</p>
     * 
     * @param value the object to extract key-value pairs from
     * @return a {@link LinkedHashMap} containing the extracted pairs
     */
    public LinkedHashMap<String, Object> extractPairs(Object value) {

        // Cast the value to a LinkedHashMap
        LinkedHashMap<?, ?> mapValue = (LinkedHashMap<?, ?>) value;
        LinkedHashMap<String, Object> newValue = new LinkedHashMap<>();
        
        // Iterate through the map's entries and add them to the new map
        for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
            String stringKey = String.valueOf(entry.getKey());
            Object mapVal = entry.getValue();
            newValue.put(stringKey, mapVal);
        }

        return newValue;
    }

    /**
     * Parses the raw data into a {@link Table}.
     * 
     * <p>This method recursively processes the data by iterating over each key-value pair and converting them
     * into {@link Column} objects. It supports both simple values (e.g., lists, primitives) and more complex,
     * nested structures (e.g., tables within tables).</p>
     * 
     * @param data the raw data to parse
     * @param fileName the name of the file, used for the table's identification
     * @return a {@link Table} object representing the parsed data
     * @throws IOException if there is an issue processing the data
     * @throws ParserException if there is a problem during parsing
     */
    private Table parse(Map<String, Object> data, String fileName) throws IOException, ParserException {

        Table table = new Table(fileName);

        // Iterate over each key-value pair in the data map
        for (String key : data.keySet()) {

            Object value = data.get(key);
            Column column = new Column(key);

            // If the value is a nested table (LinkedHashMap), recursively parse it
            if (value instanceof LinkedHashMap) {

                // Recursively process the nested table
                LinkedHashMap<String, Object> newValue = this.extractPairs(value);
                value = this.parse(newValue, key);
            }

            // If the value is a list, check if it contains nested tables or primitive types
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                boolean isLinkedHashMap = list.stream().allMatch(element -> element instanceof LinkedHashMap);

                // If the list contains nested tables, recursively parse each item
                if (isLinkedHashMap) {
                    for (Object object : (List<Object>) value) {
                        column.addRow(this.parse(this.extractPairs(object), key));
                    }
                
                // Otherwise, add the list as-is (assuming simple types like integers, strings, etc.)
                } else {
                    column.addRow(list);
                }
            } else {
                // If the value is a single item, add it directly to the column
                column.addRow(value);
            }

            // Add the column to the table
            table.addColumn(column);
        }

        return table;
    }
}
