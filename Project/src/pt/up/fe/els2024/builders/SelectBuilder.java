package pt.up.fe.els2024.builders;

import pt.up.fe.els2024.Column;
import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.SelectException;
import pt.up.fe.els2024.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code SelectBuilder} class allows for building and applying select operations on a set of tables.
 * It provides various selection options, including by path, by column, by function, and with constraints.
 * Selections can be filtered and refined based on specific conditions.
 */
public class SelectBuilder extends Builder {

    private ImportBuilder importBuilder;
    private List<Table> tables;
    private List<String> constraints;
    private String path;
    private String function = null;
    private String filterValue = null;
    private List<String> extractedValues = null;
    private Integer targetN = 1;
    private Map<String, List<Table>> folderTables = new HashMap<>();
    private Map<String, Table> FoldersMap = new HashMap<>();

    /**
     * Initializes a new SelectBuilder with specified path, constraints, and an import builder.
     * Applies initial selections based on the provided parameters.
     *
     * @param path the file path or directory path to select from
     * @param constraints a list of constraints for the selection
     * @param importBuilder the {@link ImportBuilder} instance linked to this selection
     * @param byColumn specifies whether selection is by column or not
     * @throws Exception if any error occurs during the selection process
     */
    public SelectBuilder(String path, List<String> constraints, ImportBuilder importBuilder, Boolean byColumn) throws Exception {
        this.path = path;
        this.importBuilder = importBuilder;
        this.tables = this.importBuilder.getContent();
        this.constraints = constraints;

        // Select by path, if needed
        if (path != null) {
            if (byColumn)
                selectByTargetColumn();
            else
                selectByPath(false);
        }

        // Select by constraints (composite/non-composite), if needed
        if (constraints != null && !constraints.isEmpty()) {
            selectByConstraints();
        }
    }

    /**
     * Initializes a new SelectBuilder with function-based parameters and extracted values.
     *
     * @param filterValue the filter value for the selection
     * @param function the function to apply on the selection
     * @param extractedValues a list of extracted values
     * @param importBuilder the {@link ImportBuilder} instance linked to this selection
     * @throws Exception if any error occurs during the selection process
     */
    public SelectBuilder(String filterValue, String function, List<String> extractedValues, ImportBuilder importBuilder, Integer targetN) throws Exception {
        this.filterValue = filterValue;
        this.function = function;
        this.extractedValues = extractedValues;
        this.importBuilder = importBuilder;
        this.tables = this.importBuilder.getContent();
        this.path = null;
        this.constraints = null;
        this.targetN = targetN;

        // Select by function, if available
        if (function != null && extractedValues != null && !extractedValues.isEmpty()) {
            selectByFunction();
        }
    }

    /**
     * Selects a target column from multiple tables based on the provided column name and function.
     *
     * @param columnName the name of the column to target
     * @param function the function to apply, such as "MAX" or "MIN"
     * @throws SelectException if selection fails or the function is invalid
     */
    private void selectByTargetColumn(String columnName, String function) throws SelectException {
        Table mainTable = this.tables.get(0);
        for (Table currentTable : this.tables.subList(1, this.tables.size())) {

            Number mainTableValue, currentTableValue;

            try {
                mainTableValue = (Number) mainTable.getColumn(columnName).getRows().get(0);
                currentTableValue = (Number) currentTable.getColumn(columnName).getRows().get(0);
            } catch (Exception e) {
                throw new SelectException("SelectBy must be targeted number columns");
            }

            boolean comparator = switch (function.toUpperCase()) {
                case "MAX" -> mainTableValue.doubleValue() < currentTableValue.doubleValue();
                case "MIN" -> mainTableValue.doubleValue() > currentTableValue.doubleValue();
                default -> throw new SelectException("Unknown function comparator: " + function);
            };

            if (comparator) {
                mainTable = currentTable;
            }
        }
        this.tables = new ArrayList<>(List.of(mainTable));
    }

    /**
     * Applies the specified function to target rows for selection purposes.
     *
     * @param function the name of the function to apply to the target row(s).
     * @throws SelectException if the function is invalid or the operation fails.
     */
    private void selectByTargetRow(String function) throws SelectException {

        for (Table table : this.tables) {
            if (this.folderTables.containsKey(table.getName())) {
                List<Table> helper = this.folderTables.get(table.getName());
                helper.add(table); // This works buse helper is now mutable
                this.folderTables.put(table.getName(), helper);
            } else {
                this.folderTables.put(table.getName(), new ArrayList<>(List.of(table)));
            }
        }

        for (String name : this.folderTables.keySet()){

            List<Table> tablesToBeOrdered = this.folderTables.get(name);

            if (tablesToBeOrdered.size() < this.targetN) {
                throw new SelectException("Target row is out of bounds");
            }

            if (function.equals("MAX")){
                tablesToBeOrdered.sort((t1, t2) -> {
                    if (t1.getColumns().get(0).getRows().get(0) instanceof Number && t2.getColumns().get(0).getRows().get(0) instanceof Number){
                        return Double.compare(((Number) t2.getColumns().get(0).getRows().get(0)).doubleValue(), ((Number) t1.getColumns().get(0).getRows().get(0)).doubleValue());
                    }
                    return 0;
                });

            }
            else if (function.equals("MIN")){
                tablesToBeOrdered.sort((t1, t2) -> {
                    if (t1.getColumns().get(0).getRows().get(0) instanceof Number && t2.getColumns().get(0).getRows().get(0) instanceof Number){
                        return Double.compare(((Number) t1.getColumns().get(0).getRows().get(0)).doubleValue(), ((Number) t2.getColumns().get(0).getRows().get(0)).doubleValue());
                    }
                    return 0;
                });
            }
            else {
                throw new SelectException("Unknown function comparator: " + function);
            }

            this.folderTables.put(name, tablesToBeOrdered);
        }

        this.tables.clear();

        for (String name : this.folderTables.keySet()){
            List<Table> tablesToBeAdded = this.folderTables.get(name);
            Table toBeAdded = new Table(name);
            for (int i = 0; i < this.targetN; i++){
                for (Column column : tablesToBeAdded.get(i).getColumns()) {
                    Column colToAdd = new Column(column.getName() + " #" + (i + 1));
                    colToAdd.addRow(column.getRows().getFirst());
                    toBeAdded.addColumn(colToAdd);
                }
            }

            Column fileCol = new Column("Folder");
            String folderName = Utils.extractFolderName(name);
            fileCol.addRow(folderName);

            toBeAdded.addColumn(fileCol);

            this.FoldersMap.put(folderName, toBeAdded);
        }

        Map<String, Map<String, Table>> fromFoldersMap = this.importBuilder.getFromFoldersMap();
        for (String name : fromFoldersMap.keySet()) {
            this.tables.add(this.FoldersMap.get(name));
        }
    }

    /**
     * Selects tables by targeting a column specified in the path.
     *
     * @throws SelectException if selection fails or path is ambiguous
     */
    private void selectByTargetColumn() throws SelectException {

        List<Table> selectedTables = new ArrayList<>();
        String targetColumn = Utils.getDirectory(this.path).getLast();
        List<String> reducedPath = Utils.getDirectoryPath(this.path);

        for (Table table : this.tables) {
            List<Table> subTables = table.getSubTables(reducedPath);
        
            if (subTables.size() > 1) {
                throw new SelectException("Table " + table.getName() + " is ambiguous!");
            }

            Table subTable = subTables.get(0);
            if (subTable == null) {
                throw new SelectException("Subtable " + reducedPath + " not found!");
            }

            Column column = subTable.getColumn(targetColumn);
            if (column == null) {
                throw new SelectException("Column " + targetColumn + " not found!");
            }

            Table newTable = new Table(table.getName(), new ArrayList<>(List.of(column)));
            selectedTables.add(newTable);
        }

        this.tables = selectedTables;
    }

    /**
     * Filters columns based on the extracted values specified.
     */
    private void selectByColumns() {
        List<Table> selectedTables = new ArrayList<>();

        for (Table table : this.tables) {
            List<Column> selectedColumns = new ArrayList<>();
            for (Column column : table.getColumns()) {
                if (this.extractedValues.contains(column.getName())) {
                    selectedColumns.add(column);
                }
            }
            table.setColumns(selectedColumns);
            selectedTables.add(table);
        }

        this.tables = selectedTables;
    }

    /**
     * Selects tables based on a specified function and filter value, and refines the selection.
     *
     * @throws Exception if selection fails or function is invalid
     */
    private void selectByFunction() throws Exception {

        List<String> directory = Utils.getDirectory(this.filterValue);
        String targetColumn = directory.getLast();
        this.path = String.join("/", directory.subList(0, directory.size() - 1));

        // Select available tables
        selectByPath(true);

        if (this.targetN > 1){
            selectByTargetRow(function);
        }
        else{
            // Filter the tables by the target column, and apply the function
            selectByTargetColumn(targetColumn, function);
        }

        // Filter the columns by extractedValues
        selectByColumns();
    }

    /**
     * Selects tables by their path, handling multiple tables as specified.
     *
     * @param hasMultipleTables flag indicating if multiple tables are expected in the selection
     * @throws Exception if the selection is ambiguous or path is invalid
     */
    private void selectByPath(boolean hasMultipleTables) throws Exception {

        List<String> directory = Utils.getDirectory(this.path);
        List<Table> selectedTables = new ArrayList<>();

        for (Table table : this.tables) {

            List<Table> subTables = table.getSubTables(directory);

            for (Table subTable : subTables) {
                subTable.setName(table.getName());
            }

            if (subTables == null || subTables.isEmpty()) {
                throw new SelectException("Subtable " + this.path + " not found!");
            }

            if (!hasMultipleTables && subTables.size() > 1) {
                throw new SelectException("Is ambiguous which table to select!");
            }

            selectedTables.addAll(subTables);
        }

        this.tables = selectedTables;
    }

    /**
     * Determines if a column is composite by inspecting its rows.
     *
     * @param column the column to inspect
     * @return true if the column is composite; false otherwise
     */
    private boolean isComposite(Column column) {
        boolean isComposite = false;
        for (Object row : column.getRows()) {
            isComposite |= (row instanceof Table || row instanceof List);
        }
        return isComposite;
    }

    /**
     * Selects tables based on whether they contain composite or non-composite columns.
     *
     * @param composite specifies whether to select composite or non-composite columns
     */
    private void selectByType(boolean composite) {
        List<Table> selectedTables = new ArrayList<>();
        for (Table table : this.tables) {
            List<Column> nonCompositeColumns = new ArrayList<>();
            for (Column column : table.getColumns()) {

                // Select just the composite/non-composite columns of the current table
                if (this.isComposite(column) && composite) {
                    nonCompositeColumns.add(column);
                }
            }
            // And remove the composite ones
            table.setColumns(nonCompositeColumns);
            selectedTables.add(table);
        }
        this.tables = selectedTables;
    }

    /**
     * Selects tables with non-composite columns only.
     */
    private void selectByNonComposite() {
       this.selectByType(false);
    }

    /**
     * Selects tables with composite columns only.
     */
    private void selectByComposite() {
        this.selectByType(true);
     }

    /**
     * Selects tables by constraints, such as "COMPOSITE" or "NON-COMPOSITE".
     *
     * @throws SelectException if an unknown constraint is specified
     */
    private void selectByConstraints() throws SelectException  {
        for (String constraint : this.constraints) {
            switch (constraint.toUpperCase()) {
                case "NON-COMPOSITE" -> selectByNonComposite();
                case "COMPOSITE" -> selectByComposite();
                default -> throw new SelectException("Unknown constraint " + constraint);
            }
        }
    }

    /**
     * Retrieves a column value based on the column name and table name.
     *
     * @param name the column name
     * @param tableName the table name
     * @return the formatted column value as a string
     */
    private String getColumnValue(String name, String tableName) {
        return switch(name.toUpperCase()) {
            case "FILENAME" -> tableName;
            case "FOLDERNAME" -> Utils.extractFolderName(tableName);
            case "FILEPATH" -> tableName;
            case "FOLDERPATH" -> tableName;
            default -> tableName;
        };
    }

    /**
     * Adds a new column to the selected tables with the specified name and value.
     *
     * @param columnName the name of the column to add
     * @param columnValue the value to assign to the column
     * @return this {@link SelectBuilder} instance for further configuration
     * @throws SelectException if column name or value is null
     */
    public SelectBuilder addColumn(String columnName, Object columnValue) throws SelectException {
        return this.addColumn(columnName, columnValue, false);
    }

    /**
     * Adds a new column to the selected tables with the specified name and value.
     *
     * @param columnName the name of the column to add
     * @param columnValue the value to assign to the column
     * @param insertAtStart
     * @return this {@link SelectBuilder} instance for further configuration
     * @throws SelectException if column name or value is null
     */
    public SelectBuilder addColumn(String columnName, Object columnValue, boolean insertAtStart) throws SelectException {

        if (columnName == null || columnValue == null) {
            throw new SelectException("Column name or value is null");
        }

        List<Table> resultTables = new ArrayList<>();

        for (int i = 0; i < this.tables.size(); i++) {

            Object value = columnValue;

            if (value instanceof String) {
                value = this.getColumnValue((String) columnValue, this.tables.get(i).getName());
            }

            List<Column> columns = new ArrayList<>(this.tables.get(i).getColumns());
            Table newTable = new Table(this.tables.get(i).getName(), columns);

            Column column = new Column(columnName, new ArrayList<>(List.of(value)));

            newTable.addColumn(column, insertAtStart);
            resultTables.add(newTable);
        }

        this.tables = resultTables;
        return this;
    }
    
    /**
     * Completes the selection and returns the associated {@link ImportBuilder} for further operations.
     *
     * @return the {@link ImportBuilder} linked to this selection
     */
    public ImportBuilder end() {
        importBuilder.setTables(this.tables);
        return importBuilder;
    }
}
