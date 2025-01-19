package pt.up.fe.els2024.export;

import java.io.IOException;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;
import pt.up.fe.els2024.utils.Utils;

/**
 * The {@code CSVExporter} class provides functionality to export {@link Table} data
 * to a CSV (Comma-Separated Values) file. It handles exporting nested tables by 
 * creating separate CSV files for each subtable and linking them in the main table.
 */
public class CSVExporter extends Exporter {

    /** The separator used between columns in the CSV file. */
    static final String SEPARATOR = ",";

    /**
     * Exports the given {@link Table} to a CSV file at the specified file path.
     *
     * @param table the {@link Table} instance to be exported
     * @param tableName the name of the table, used in naming subtables
     * @param filePath the file path where the CSV file will be written
     * @throws IOException if an I/O error occurs during file writing
     * @throws ExportException if an error specific to export occurs
     */
    public static void export(Table table, String tableName, String filePath) throws IOException, ExportException {
        StringBuilder stringBuilder = new StringBuilder();
        
        // Writing the header
        for (Column column : table.getColumns()) {
            String columnName = column.getName();
            if (columnName != null) {
                stringBuilder.append(columnName).append(SEPARATOR);
            }
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append("\n");

        // Find the maximum number of rows
        int maxRows = getMaxRows(table);

        // Write data row by row
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {

            // For each column in this row
            for (Column column : table.getColumns()) {
                if (rowIndex < column.getRows().size()) {
                    Object row = column.getRows().get(rowIndex);

                    // The cell is another table
                    if (row instanceof Table) {
                        
                        String columnName = column.getName();
                        String newTableName = tableName + "-" + columnName;
                        String newFileName = Utils.extractFileName(filePath) + "-" + columnName + ".csv";
                        String newTablePath = Utils.removeExtension(filePath) + "-" + columnName + ".csv";

                        // Creates a separate file for this subtable...
                        CSVExporter.export((Table) row, newTableName, newTablePath);

                        // ... and creates a link to it
                        stringBuilder.append(newFileName);

                    // The cell is a primitive or list (String, Integer, List<T>)
                    } else {
                        stringBuilder.append(column.getRows().get(rowIndex));
                    }
                }
                stringBuilder.append(SEPARATOR);
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("\n");
        }

        Exporter.writeToFile(stringBuilder.toString(), filePath);
    }
}
