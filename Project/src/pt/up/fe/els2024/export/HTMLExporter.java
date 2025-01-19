package pt.up.fe.els2024.export;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;
import pt.up.fe.els2024.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code HTMLExporter} class provides functionality for exporting {@link Table} data 
 * into an HTML format, where each table's columns are displayed in an HTML table.
 * 
 * <p>This class includes utilities for rendering HTML table structures, including styles for table cells
 * and the handling of nested tables, where sub-table data is exported as separate HTML files, linked within the main table.</p>
 */
public class HTMLExporter extends Exporter {

    /**
     * Returns the CSS used to style the HTML table in the export.
     * 
     * @return a string containing the CSS styling for the HTML table
     */
    private static String importCSS() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("        table {\n");
        stringBuilder.append("            width: 100%;\n");
        stringBuilder.append("            border-collapse: collapse;\n");
        stringBuilder.append("        }\n");
        stringBuilder.append("        th, td {\n");
        stringBuilder.append("            border: 1px solid #dddddd;\n");
        stringBuilder.append("            text-align: left;\n");
        stringBuilder.append("            padding: 8px;\n");
        stringBuilder.append("        }\n");
        stringBuilder.append("        th {\n");
        stringBuilder.append("            background-color: #f2f2f2;\n");
        stringBuilder.append("        }\n");

        return stringBuilder.toString();
    }

    /**
     * Creates the HTML header, including meta information and the opening table tag.
     *
     * @param tableName the name of the table being exported
     * @return a string representing the HTML header
     */
    private static String createHTMLHeader(String tableName) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<!DOCTYPE html>\n");
        stringBuilder.append("<html lang=\"en\">\n");
        stringBuilder.append("<head>\n");
        stringBuilder.append("    <meta charset=\"UTF-8\">\n");
        stringBuilder.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        stringBuilder.append("    <title>Table: ");
        stringBuilder.append(tableName);
        stringBuilder.append("</title>\n");
        stringBuilder.append("    <style>\n");
        stringBuilder.append(HTMLExporter.importCSS());
        stringBuilder.append("    </style>\n");
        stringBuilder.append("</head>\n");
        stringBuilder.append("<body>\n");
        stringBuilder.append("    <h1>Table: ");
        stringBuilder.append(tableName);
        stringBuilder.append("</h1>\n");
        stringBuilder.append("    <table>\n");

        return stringBuilder.toString();
    }

    /**
     * Creates the HTML footer, closing the table and body tags.
     *
     * @return a string representing the HTML footer
     */
    private static String createHTMLFooter() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("    </table>\n");
        stringBuilder.append("</body>\n");
        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }

    /**
     * Exports a {@link Table} into an HTML file with the specified name and file path.
     * Nested tables are recursively exported into separate HTML files and linked within the main table.
     *
     * @param table the {@link Table} to export
     * @param tableName the name of the table for the HTML page title and file name
     * @param filePath the path where the HTML file will be saved
     * @throws IOException if an error occurs while writing the HTML file
     * @throws ExportException if an error occurs during the export process
     */
    public static void export(Table table, String tableName, String filePath) throws IOException, ExportException {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(createHTMLHeader(tableName));

        // Writing table header
        stringBuilder.append("        <tr>\n");
        for (Column column : table.getColumns()) {
            stringBuilder.append("            <th>").append(column.getName()).append("</th>\n");
        }
        stringBuilder.append("        </tr>\n");

        // Find the maximum number of rows
        int maxRows = getMaxRows(table);

        // Write data row by row
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            stringBuilder.append("        </tr>\n");

            // For each column in this row
            for (Column column : table.getColumns()) {
                stringBuilder.append("            <td>");
                if (rowIndex < column.getRows().size()) {
                    Object row = column.getRows().get(rowIndex);

                    // The cell is another table
                    if (row instanceof Table) {

                        String columnName = column.getName();
                        String rowID = column.getRows().size() > 1 ? ( "-" + rowIndex ) : "";
                        String newTableName = tableName + "-" + columnName + rowID;
                        String newTablePath = Utils.removeExtension(filePath) + "-" + columnName + rowID + ".html";
                        String newFilePath = Utils.extractFileName(filePath) + "-" + columnName + rowID + ".html";

                        // Creates a separate file for this table...
                        HTMLExporter.export((Table) row, newTableName, newTablePath);

                        // ... and creates a link to it
                        stringBuilder.append("<a href='");
                        stringBuilder.append(newFilePath);
                        stringBuilder.append("'>");
                        stringBuilder.append(newTableName);
                        stringBuilder.append("</a>");

                    // The cell is a primitive or list (String, Integer, List<T>)
                    } else {

                        // If it is a List, we need to ensure the correct output
                        if (row instanceof List) {

                            // Escape `<` and `>` in each element of the list
                            row = ((List<Object>) row)
                                    .stream()
                                    .map(Object::toString)
                                    .map(item -> item.replace("<", "&lt;")
                                                     .replace(">", "&gt;")
                                    ).collect(Collectors.joining(", "));
                        }

                        stringBuilder.append(row);
                    }
                }

                stringBuilder.append("</td>\n");
            }

            stringBuilder.append("        </tr>\n");
        }

        stringBuilder.append(createHTMLFooter());
        Exporter.writeToFile(stringBuilder.toString(), filePath);
    }
}
