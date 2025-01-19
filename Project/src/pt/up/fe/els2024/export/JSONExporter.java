package pt.up.fe.els2024.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;

/**
 * The {@code JSONExporter} class provides functionality for exporting a {@link Table} to a JSON format.
 * The class converts the table's columns and their associated rows into a map structure and then
 * serializes that map into a well-formatted JSON string using the Gson library.
 */
public class JSONExporter extends Exporter {

    /**
     * Converts the columns and rows of a given table into a nested map structure suitable for JSON serialization.
     * 
     * <p>This method recursively converts each column's rows into a list of elements, handling nested tables as well.</p>
     * 
     * @param table the {@link Table} to convert to a map
     * @return a map representing the table in a JSON-like structure, with column names as keys
     */
    public static Map<String, Object> mapCreator(Table table) {
        Map<String, Object> jsonMap = new HashMap<>();

        // Iterate over each column of the table
        for (Column column : table.getColumns()) {

            List<Object> rows = column.getRows();
            List<Object> elements = new ArrayList<>();

            // Iterate over each row in the column
            for (Object row : rows) {
                // If the row is a nested table, recursively convert it
                if (row instanceof Table) {
                    row = JSONExporter.mapCreator((Table) row);
                }
                elements.add(row);
            }

            // If the column has only one element, store it as a simple value
            // Otherwise, store it as a list of elements
            Object value = elements.size() == 1 ? elements.get(0) : elements;
            jsonMap.put(column.getName(), value);
        }

        return jsonMap;
    }

    /**
     * Exports a {@link Table} to a JSON file at the specified file path.
     * 
     * <p>This method converts the table to a JSON representation by first mapping the table to a 
     * {@link Map} structure and then serializing it using the Gson library. The output is pretty-printed.</p>
     *
     * @param table the {@link Table} to export
     * @param filePath the path where the JSON file will be saved
     * @throws IOException if an error occurs while writing the JSON file
     * @throws ExportException if an error occurs during the export process
     */
    public static void export(Table table, String filePath) throws IOException, ExportException {

        // Convert the table into a JSON-compatible map
        Map<String, Object> jsonMap = JSONExporter.mapCreator(table);

        // Create a Gson instance to pretty-print the JSON output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(jsonMap);

        // Write the JSON string to the specified file
        Exporter.writeToFile(jsonString, filePath);
    }
}
