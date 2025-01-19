package pt.up.fe.els2024.export;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@code Exporter} class provides base functionality for exporting {@link Table} data.
 * It includes utilities for writing content to files and finding the maximum number of rows
 * in a table, which is useful when formatting tabular data consistently across different file formats.
 * 
 * This class is intended to be extended by format-specific exporters (e.g., {@code CSVExporter}).
 */
public class Exporter {
    
    /**
     * Placeholder method for export functionality. Throws an {@link ExportException} 
     * if called, as subclasses are expected to implement specific export logic.
     *
     * @throws ExportException indicating that the export method is not implemented
     */
    public static void export() throws ExportException {
        throw new ExportException("Export method not implemented!");
    }

    /**
     * Calculates the maximum number of rows across all columns in the given {@link Table}.
     *
     * @param table the table from which to find the maximum row count
     * @return the maximum row count among all columns in the table
     */
    public static int getMaxRows(Table table) {
        int maxRows = 0;
        for (Column column : table.getColumns()) {
            maxRows = Math.max(maxRows, column.getRows().size());
        }
        return maxRows;
    }

    /**
     * Writes the specified content to a file with the given file name.
     *
     * @param content the content to be written to the file
     * @param fileName the name (or path) of the file where the content will be written
     * @throws ExportException if an export-specific error occurs
     * @throws IOException if an I/O error occurs during file writing
     */
    public static void writeToFile(String content, String fileName) throws ExportException, IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(content);
        fileWriter.close();
    }
}
