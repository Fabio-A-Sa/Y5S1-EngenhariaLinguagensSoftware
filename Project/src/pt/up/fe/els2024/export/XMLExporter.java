package pt.up.fe.els2024.export;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code XMLExporter} class provides functionality for exporting a {@link Table} to an XML format.
 * It converts the table's columns and their associated rows into a map structure and then serializes that map into an XML string using the Jackson XML library.
 */
public class XMLExporter extends Exporter {

    /**
     * Converts the columns and rows of a given table into a nested map structure suitable for XML serialization.
     * 
     * <p>This method recursively converts each column's rows into a list of elements, handling nested tables as well.</p>
     * 
     * @param table the {@link Table} to convert to a map
     * @return a map representing the table in an XML-like structure, with column names as keys
     */
    public static Map<String, Object> mapCreator(Table table) {
        Map<String, Object> xmlMap = new HashMap<>();

        // Iterate over each column of the table
        for (Column column : table.getColumns()) {
            List<Object> rows = column.getRows();
            List<Object> elements = new ArrayList<>();

            // Iterate over each row in the column
            for (Object row : rows) {
                // If the row is a nested table, recursively convert it
                if (row instanceof Table) {
                    row = XMLExporter.mapCreator((Table) row);
                }
                elements.add(row);
            }

            // If the column has only one element, store it as a simple value
            // Otherwise, store it as a list of elements
            Object value = elements.size() == 1 ? elements.get(0) : elements;
            xmlMap.put(column.getName(), value);
        }

        return xmlMap;
    }

    /**
     * Exports a {@link Table} to an XML file at the specified file path.
     * 
     * <p>This method converts the table to an XML representation by first mapping the table to a 
     * {@link Map} structure and then serializing it using the Jackson XML library. The output is pretty-printed.</p>
     *
     * @param table the {@link Table} to export
     * @param filePath the path where the XML file will be saved
     * @throws IOException if an error occurs while writing the XML file
     * @throws ExportException if an error occurs during the export process
     */
    public static void export(Table table, String filePath) throws IOException, ExportException {

        // Create a map to store the table data, with the table name as the root element
        Map<String, Object> xmlMap = new HashMap<>();
        Map<String, Object> tableData = XMLExporter.mapCreator(table);
        xmlMap.put(table.getName(), tableData);

        // Create an XmlMapper to convert the map to XML format
        XmlMapper xmlMapper = new XmlMapper();
        String xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xmlMap);

        // Write the XML string to the specified file
        Exporter.writeToFile(xmlString, filePath);
    }
}
