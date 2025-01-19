package pt.up.fe.els2024.builders;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.ParserException;
import pt.up.fe.els2024.parser.Parser;
import pt.up.fe.els2024.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A builder class for importing table data from files and directories.
 * Supports file parsing, folder import with extension filtering, and merging tables.
 * Designed for use with {@link OperationBuilder} to perform further table operations.
 */
public class ImportBuilder extends Builder {

    /** The resulting table after merging imported tables. */
    private Table resulTable;

    /** A list of tables parsed from files. */
    private List<Table> tables = new ArrayList<>();

    /** A parser instance to read files. */
    private Parser parser = new Parser();

    /** The operation builder to perform subsequent operations on the tables. */
    private OperationBuilder operationBuilder;

    /** The path of the current file being processed. */
    private String filePath;

    /** The name of the current file being processed. */
    private String fileName;

    private Map<String, Map<String, Table>> fromFoldersMap = new HashMap<>(); //Key: path, Value: Extension

    /**
     * Constructs an ImportBuilder instance with a specified name and operation builder.
     *
     * @param name                    the name associated with this import operation.
     * @param columnOperationBuilder  an instance of {@link OperationBuilder} for subsequent table operations.
     */
    public ImportBuilder(String name, OperationBuilder columnOperationBuilder) {
        Builder.name = name;
        this.operationBuilder = columnOperationBuilder;
        this.resulTable = this.operationBuilder.getTable();
    }

    /**
     * Retrieves the list of tables imported by this builder.
     *
     * @return a list of {@link Table} instances.
     */
    public List<Table> getContent() {
        return this.tables;
    }

    /**
     * Retrieves the map of folder names to their corresponding tables.
     *
     * @return a map where the keys are folder names and the values are maps of table names to {@link Table} instances.
     */
    public Map<String, Map<String, Table>> getFromFoldersMap() {
        return this.fromFoldersMap;
    }

    /**
     * Imports data from a specified file.
     *
     * @param filePath the path to the file to be parsed.
     * @return the current instance of {@code ImportBuilder}.
     * @throws ParserException if an error occurs during parsing.
     * @throws IOException     if an I/O error occurs.
     */
    public ImportBuilder fromFile(String filePath) throws ParserException, IOException {
        File file = new File(filePath);
        this.tables.add(parser.parseFile(file));
        this.filePath = filePath;
        this.fileName = Utils.extractFileName(filePath);
        return this;
    }

    /**
     * Imports all files from a specified directory.
     *
     * @param folderDirectory the directory containing files to parse.
     * @return the current instance of {@code ImportBuilder}.
     * @throws ParserException if an error occurs during parsing.
     * @throws IOException     if an I/O error occurs.
     */
    public ImportBuilder fromFolder(String folderDirectory) throws ParserException, IOException {
        File folder = new File(folderDirectory);
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            this.fromFile(folderDirectory + file.getName());
        }
        return this;
    }

    /**
     * Imports files with a specific extension from a specified directory.
     *
     * @param folderDirectory  the directory containing files to parse.
     * @param targetExtension  the extension of files to import.
     * @return the current instance of {@code ImportBuilder}.
     * @throws ParserException if an error occurs during parsing.
     * @throws IOException     if an I/O error occurs.
     */
    public ImportBuilder fromFolder(String folderDirectory, String targetExtension) throws ParserException, IOException {
        File folder = new File(folderDirectory);
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            String fileExtension = Utils.getFileExtension(file);
            if (fileExtension.equals(targetExtension))
                this.fromFile(folderDirectory + file.getName());
        }
        return this;
    }

    /**
     * Selects tables by specified path and constraints.
     *
     * @param path        the path to filter tables.
     * @param constraints the list of constraints for filtering.
     * @return an instance of {@link SelectBuilder}.
     * @throws Exception if an error occurs during selection.
     */
    public SelectBuilder selectByTable(String path, List<String> constraints) throws Exception {
        return new SelectBuilder(path, constraints, this, false);
    }

    /**
     * Selects columns by specified path and constraints.
     *
     * @param path        the path to filter columns.
     * @param constraints the list of constraints for filtering.
     * @return an instance of {@link SelectBuilder}.
     * @throws Exception if an error occurs during selection.
     */
    public SelectBuilder selectByColumn(String path, List<String> constraints) throws Exception, ParserException {
        return new SelectBuilder(path, constraints, this, true);
    }

    /**
     * Parses a directory structure and maps folder names to tables contained in their files.
     * 
     * @param foldersPath the path to the root directory containing subfolders and table files.
     * @return the current {@link ImportBuilder} instance for method chaining.
     * @throws ParserException if an error occurs while parsing a table file.
     * @throws IOException if an I/O error occurs while accessing the files or directories.
     * @throws IllegalArgumentException if the provided path is not a valid directory.
     * @throws IllegalStateException if the subfolders of the directory cannot be listed.
     */
    public ImportBuilder fromFolders(String foldersPath) throws ParserException, IOException {

        File rootFolder = new File(foldersPath);

        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid path: " + foldersPath);
        }

        File[] subFolders = rootFolder.listFiles(File::isDirectory);
        if (subFolders == null) {
            throw new IllegalStateException("Can't find sub-folders on the path: " + foldersPath);
        }

        for (File subFolder : subFolders) {
            String subFolderName = subFolder.getName();

            Map<String, Table> tablesMap = new HashMap<>();

            File[] tableFiles = subFolder.listFiles(File::isFile);
            if (tableFiles != null) {
                for (File tableFile : tableFiles) {
                    String tableName = tableFile.getName();

                    Table table = parser.parseFile(new File(foldersPath + "/" + subFolderName + "/" + tableFile.getName()));
                    table.setName(foldersPath + subFolderName + "/" + tableName);
                    tablesMap.put(tableName, table);
                }
            }

            fromFoldersMap.put(subFolderName, tablesMap);
        }

        return this;
    }

    /**
     * Selects values based on filter criteria.
     *
     * @param filterValue     the filter value for selection.
     * @param function        the function used for filtering.
     * @param extractedValues the list of values to be extracted.
     * @return an instance of {@link SelectBuilder}.
     * @throws Exception if an error occurs during selection.
     */
    public SelectBuilder selectByFilter(String filterValue, String function, List<String> extractedValues) throws Exception {
        return new SelectBuilder(filterValue, function, extractedValues, this, 1);
    }

    /**
     * Creates a {@link SelectBuilder} to filter and select up to a target number of values based on the provided filter.
     *
     * @param filterValue the value used to filter the selection.
     * @param function the function applied during the selection process.
     * @param extractedValues a list of values to be used in the selection process.
     * @param targetN the maximum number of items to select.
     * @return a new {@link SelectBuilder} instance initialized with the provided parameters.
     * @throws Exception if an error occurs during the creation of the {@link SelectBuilder}.
     */
    public SelectBuilder selectNByFilter(String filterValue, String function, List<String> extractedValues, Integer targetN) throws Exception{
        return new SelectBuilder(filterValue, function, extractedValues, this, targetN);
    }

    /**
     * Sets the list of tables to the specified value.
     *
     * @param tables a list of {@link Table} instances to set.
     */
    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    /**
     * Merges all imported tables by columns or rows, adding columns or rows from each table
     * to the result table.
     */
    private void merge() {
        Table firstTable = new Table(this.filePath);
        for (Table table : this.tables) {
            for (Column column : table.getColumns()) {
                for (Object row : column.getRows()) {
                    if (!firstTable.appendToColumn(column.getName(), row)) {
                        firstTable.addColumn(column);
                    }
                }
            }
        }
        this.resulTable = firstTable;
    }

    /**
     * Merges all imported tables based on the number of tables imported.
     * If only one table is imported, it becomes the result table.
     * If multiple tables are imported, they are merged by columns or by rows.
     */
    private void mergeTables() {
        if (tables.isEmpty()) return;
        if (tables.size() == 1) {
            this.resulTable = this.tables.get(0);
        } else {
            this.merge();
        }
    }

    /**
     * Merges all tables into a single table by consolidating their columns and rows.
     * If a column already exists in the first table, rows are appended to it; 
     * otherwise, a new column is added to the first table.
     */
    private void mergeTablesSingleLine(){
        Table firstTable = new Table(this.filePath);
        for (Table table : this.tables) {
            for (Column column : table.getColumns()) {
                for (Object row : column.getRows()) {
                    if (!firstTable.appendToColumn(column.getName(), row)) {
                        firstTable.addColumn(column);
                    }
                }
            }
        }
        this.resulTable = firstTable;
    }

    /**
     * Retrieves the name of the current file being processed.
     *
     * @return the file name as a string.
     */
    public String getFileName(){
        return this.fileName;
    }

    /**
     * Retrieves the path of the current file being processed.
     *
     * @return the file path as a string.
     */
    public String getFilePath(){
        return this.filePath;
    }

    /**
     * Filters the tables from `fromFoldersMap` by the specified file extension and adds them to the builder's table list.
     *
     * @param extension the file extension to filter tables by (e.g., "csv", "txt").
     * @return the current instance of {@link ImportBuilder} for method chaining.
     */
    public ImportBuilder whenExtension(String extension) {
        for (Map.Entry<String, Map<String, Table>> subFolderEntry : this.fromFoldersMap.entrySet()) {
            String subFolderName = subFolderEntry.getKey();
            Map<String, Table> tablesMap = subFolderEntry.getValue();

            for (Map.Entry<String, Table> tableEntry : tablesMap.entrySet()) {
                String tableName = tableEntry.getKey();
                Table table = tableEntry.getValue();
                String tableExtension = Utils.getExtension(tableName);

                if (tableExtension.equals(extension)) {
                    tables.add(table);
                }
            }
        }
        return this;
    }

    /**
     * Finalizes the "when" operation by merging all filtered tables into a single table
     * and passing the result to the `operationBuilder`. Clears the current table list 
     * to prepare for further operations.
     *
     * @return the current instance of {@link ImportBuilder} for method chaining.
     */
    public ImportBuilder endWhen() {
        this.mergeTablesSingleLine();
        tables.clear();
        //tables.add(this.resulTable);

        this.operationBuilder.mergeTables(this.resulTable);

        this.resulTable = new Table();
        return this;
    }

    /**
     * Finalizes the import process, merges tables, and returns the associated operation builder.
     *
     * @return the associated {@link OperationBuilder} instance.
     */
    public OperationBuilder end() {
        this.mergeTables();
        this.operationBuilder.mergeTables(this.resulTable);
        return this.operationBuilder;
    }
}
