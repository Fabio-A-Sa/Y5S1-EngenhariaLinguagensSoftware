package pt.up.fe.els2024.builders;

import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ExportException;
import pt.up.fe.els2024.export.CSVExporter;
import pt.up.fe.els2024.export.HTMLExporter;
import pt.up.fe.els2024.export.JSONExporter;
import pt.up.fe.els2024.export.XMLExporter;
import pt.up.fe.els2024.utils.Utils;

import java.io.IOException;

/**
 * The {@code TableBuilder} class provides a builder pattern for constructing, configuring, 
 * and exporting tables in various formats. It supports setting table properties and 
 * exporting the table to specified file formats.
 */
public class TableBuilder extends Builder {

    private Table table;

    /**
     * Constructs a new {@code TableBuilder} with an empty {@link Table} instance.
     */
    public TableBuilder() {
        this.table = new Table();
    }

    /**
     * Sets the table instance for this builder.
     *
     * @param table the {@link Table} instance to be set
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Retrieves the current table instance from this builder.
     *
     * @return the current {@link Table} instance
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Completes the table building process, returning this {@code TableBuilder} instance.
     *
     * @return this {@code TableBuilder} instance
     */
    public TableBuilder end() {
        return this;
    }

    /**
     * Sets the name of the table.
     *
     * @param name the name to assign to the table
     * @return this {@code TableBuilder} instance, for method chaining
     */
    public TableBuilder withName(String name) {
        this.table.setName(name);
        return this;
    }

    /**
     * Initiates an {@link OperationBuilder} to perform operations on the table.
     *
     * @param operationName the name of the operation to perform
     * @return a new {@link OperationBuilder} instance for the specified operation
     */
    public OperationBuilder performOperation(String operationName) {
        return new OperationBuilder(operationName, this);
    }

    /**
     * Assembles and exports the table to a specified file path, supporting CSV, HTML, JSON, and XML formats.
     *
     * @param outputFilePath the file path to export the table to
     * @return the assembled {@link Table} instance
     * @throws IOException if an I/O error occurs during export
     * @throws ExportException if the file extension is unsupported
     */
    public Table assemble(String outputFilePath) throws IOException, ExportException {
        String tableName = this.table.getName();
        String extension = Utils.getExtension(outputFilePath);

        switch (extension) {
            case "csv" -> CSVExporter.export(this.table, tableName, outputFilePath);
            case "html" -> HTMLExporter.export(this.table, tableName, outputFilePath);
            case "json" -> JSONExporter.export(this.table, outputFilePath);
            case "xml" -> XMLExporter.export(this.table, outputFilePath);
            default -> throw new ExportException("Unknown export extension: " + extension);
        }

        return this.table;
    }
}
