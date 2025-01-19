package pt.up.fe.els2024;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.els2024.exception.OperationException;

/**
 * {@code Table} represents a table structure with columns and rows.
 * A table has a name and a list of columns, where each column can contain multiple rows.
 * <p>
 * This class provides methods to manipulate columns, including adding, removing, renaming columns,
 * and retrieving columns by name. It also supports adding rows to columns and extracting subtables.
 * </p>
 */
public class Table {

    private String name;
    private List<Column> columns;

    /**
     * Default constructor initializes a table with no name and an empty list of columns.
     */
    public Table() {
        this.name = null;
        this.columns = new ArrayList<>();
    }

    /**
     * Constructor to initialize a table with a specific name.
     * The columns list will be empty upon creation.
     *
     * @param name the name of the table
     */
    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    /**
     * Constructor to initialize a table with a specific name and a given list of columns.
     *
     * @param name    the name of the table
     * @param columns the list of columns to initialize the table with
     */
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Retrieves the name of the table.
     *
     * @return the name of the table
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a new name for the table.
     *
     * @param name the new name to assign to the table
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Adds a new column to the table.
     *
     * @param column the column to add
     */
    public void addColumn(Column column) {
        this.columns.add(column);
    }

    /**
     * Adds a new column to the table, optionally inserting it at the start.
     *
     * @param column        the column to add
     * @param insertAtStart if {@code true}, inserts the column at the start of the columns list, otherwise adds it at the end
     */
    public void addColumn(Column column, boolean insertAtStart) {
        if (insertAtStart) {
            List<Column> newColumns = new ArrayList<>();
            newColumns.add(column);
            newColumns.addAll(this.columns);
            this.columns = newColumns;
        } else {
            this.columns.add(column);
        }
    }

    /**
     * Sets the columns of the table with a given list of columns.
     *
     * @param columns the list of columns to set
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Appends additional columns to the table.
     *
     * @param columns the list of columns to append
     */
    public void appendColumns(List<Column> columns) {
        this.columns.addAll(columns);
    }

    /**
     * Retrieves a column by its name.
     *
     * @param name the name of the column
     * @return the column with the specified name, or {@code null} if no such column exists
     */
    public Column getColumn(String name) {
        for (Column column : this.columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    /**
     * Retrieves the list of columns in the table.
     *
     * @return a list of columns in the table
     */
    public List<Column> getColumns(){
        return this.columns;
    }

    /**
     * Appends a new value to a specified column.
     *
     * @param columnName the name of the column to append the value to
     * @param value      the value to append to the column
     * @return {@code true} if the value was successfully appended, {@code false} otherwise
     */
    public boolean appendToColumn(String columnName, Object value) {
        for (Column column : this.columns) {
            if (column.getName().equals(columnName)) {
                column.addRow(value);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a column exists by its name.
     *
     * @param columnName the name of the column to check
     * @return the column if it exists
     * @throws OperationException if the column does not exist
     */
    public Column checkColumn(String columnName) throws OperationException {
        Column column = this.getColumn(columnName);
        if (column == null) {
            throw new OperationException("Column " + columnName + " doesn't exist");
        }
        return column;
    }

    /**
     * Renames an existing column.
     *
     * @param columnOldName the old name of the column
     * @param columnNewName the new name of the column
     * @throws OperationException if the old column name doesn't exist
     */
    public void renameColumn(String columnOldName, String columnNewName) throws OperationException {
        this.checkColumn(columnOldName);
        int index = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).getName().equals(columnOldName)) {
                index = i;
                break;
            }
        }
        this.columns.get(index).setName(columnNewName);
    }

    /**
     * Removes a column by its name.
     *
     * @param columnName the name of the column to remove
     * @throws OperationException if the column doesn't exist
     */
    public void remove(String columnName) throws OperationException {
        this.checkColumn(columnName);
        Column column = this.getColumn(columnName);
        this.columns.remove(column);
    }

    /**
     * Retrieves a list of subtables by traversing through nested columns and matching the directory path.
     * The directory path is a list of column names to navigate through the nested structure.
     *
     * @param directory the directory path to match
     * @return a list of subtables matching the directory path
     */
    public List<Table> getSubTables(List<String> directory) {

        List<Table> tables = new ArrayList<>();

        // Base case: if the directory is empty, return the current table
        if (directory.isEmpty()) {
            tables.add(this);
            return tables;
        }

        // Process the first part of the directory path
        String dir = directory.get(0);
        Column column = this.getColumn(dir);

        if (column != null) {
            for (Object row : column.getRows()) {
                if (row instanceof Table) {
                    // Recursive call to find subtables
                    List<Table> listTables = ((Table) row).getSubTables(directory.subList(1, directory.size()));
                    tables.addAll(listTables);
                }
            }
            return tables;
        }

        // Column not found
        return null;
    }
}
