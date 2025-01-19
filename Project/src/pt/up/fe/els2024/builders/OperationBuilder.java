package pt.up.fe.els2024.builders;

import pt.up.fe.els2024.Table;
import pt.up.fe.els2024.exception.OperationException;
import pt.up.fe.els2024.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A builder class for performing operations on tables and columns.
 * Allows adding, renaming, and removing columns, as well as performing
 * mathematical operations between numeric columns. Designed for use with
 * {@link TableBuilder} to build and modify tables.
 */
public class OperationBuilder extends Builder {

    /** The table builder associated with this operation builder. */
    private final TableBuilder tableBuilder;

    /** The table on which operations are performed. */
    private Table resultTable;
    private int addedRows = 0;

    /**
     * Constructs an OperationBuilder instance with a specified name and table builder.
     *
     * @param name         the name associated with this operation.
     * @param tableBuilder the {@link TableBuilder} instance to be used for further modifications.
     */
    public OperationBuilder(String name, TableBuilder tableBuilder) {
        Builder.name = name;
        this.tableBuilder = tableBuilder;
        this.resultTable = this.tableBuilder.getTable();
    }

    /**
     * Initiates an import process for tables.
     *
     * @return an instance of {@link ImportBuilder}.
     */
    public ImportBuilder withImport() {
        return new ImportBuilder("Import", this);
    }

    /**
     * Adds a new column with a specified name and value to the table.
     *
     * @param columnName  the name of the new column.
     * @param columnValue the value to populate the column.
     * @return the current instance of {@code OperationBuilder}.
     */
    public OperationBuilder addColumn(String columnName, Object columnValue) {
        List<Object> rows = new ArrayList<>(List.of(columnValue));
        Column column = new Column(columnName, rows);
        this.resultTable.addColumn(column);
        return this;
    }

    /**
     * Adds a new column with a specified name and value, with an option to insert at the start.
     *
     * @param columnName    the name of the new column.
     * @param columnValue   the value to populate the column.
     * @param insertAtStart if true, inserts the column at the start of the table.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the column value is {@code null}.
     */
    public OperationBuilder addColumn(String columnName, Object columnValue, boolean insertAtStart) throws OperationException {
        if (columnValue == null) {
            throw new OperationException("Column name is null");
        }
        List<Object> rows = new ArrayList<>(List.of(columnValue));
        Column column = new Column(columnName, rows);
        this.resultTable.addColumn(column, insertAtStart);
        return this;
    }

    /**
     * Verifies the existence of a column with the specified name in the table.
     *
     * @param columnName the name of the column to check.
     * @return the {@link Column} instance if it exists.
     * @throws OperationException if the column does not exist.
     */
    private Column checkColumn(String columnName) throws OperationException {
        return this.resultTable.checkColumn(columnName);
    }

    /**
     * Performs a specified mathematical operation on two numeric columns.
     *
     * @param columnName1      the name of the first column.
     * @param columnName2      the name of the second column.
     * @param resultColumnName the name of the result column.
     * @param operation        a {@link BiFunction} defining the operation to perform.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if columns have different sizes or contain non-numeric values.
     */
    private OperationBuilder operate(String columnName1, String columnName2, String resultColumnName,
                                     BiFunction<Number, Number, Number> operation) throws OperationException {
        List<Object> column1rows = this.checkColumn(columnName1).getRows();
        List<Object> column2rows = this.checkColumn(columnName2).getRows();

        if (column1rows.size() != column2rows.size()) {
            throw new OperationException("Cannot operate on columns with different sizes");
        }

        boolean justNumbers = column1rows.stream().allMatch(element -> element instanceof Number) 
                                && column2rows.stream().allMatch(element -> element instanceof Number);
        if (!justNumbers) {
            throw new OperationException("Cannot operate on columns with non-numbers");
        }

        List<Object> resultRows = new ArrayList<>();
        for (int i = 0 ; i < column1rows.size() ; i++) {
            Number n1 = (Number) column1rows.get(i);
            Number n2 = (Number) column2rows.get(i);
            Number result = operation.apply(n1, n2);
            result = Math.round(result.doubleValue() * 100.0) / 100.0;
            resultRows.add(result);
        }

        Column resultColumn = new Column(resultColumnName, resultRows);
        this.resultTable.addColumn(resultColumn);
        return this;
    }

    /**
     * Adds a new column with values obtained by summing corresponding elements in two columns.
     *
     * @param columnName1      the name of the first column.
     * @param columnName2      the name of the second column.
     * @param resultColumnName the name of the result column.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the columns contain non-numeric values or have different sizes.
     */
    public OperationBuilder sum(String columnName1, String columnName2, String resultColumnName) throws OperationException {
        return operate(columnName1, columnName2, resultColumnName, (n1, n2) -> n1.doubleValue() + n2.doubleValue());
    }

    /**
     * Adds a new column with values obtained by subtracting elements of the second column from the first.
     *
     * @param columnName1      the name of the first column.
     * @param columnName2      the name of the second column.
     * @param resultColumnName the name of the result column.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the columns contain non-numeric values or have different sizes.
     */
    public OperationBuilder sub(String columnName1, String columnName2, String resultColumnName) throws OperationException {
        return operate(columnName1, columnName2, resultColumnName, (n1, n2) -> n1.doubleValue() - n2.doubleValue());
    }

    /**
     * Adds a new column with values obtained by dividing elements of the first column by the second.
     *
     * @param columnName1      the name of the first column.
     * @param columnName2      the name of the second column.
     * @param resultColumnName the name of the result column.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the columns contain non-numeric values or have different sizes.
     */
    public OperationBuilder div(String columnName1, String columnName2, String resultColumnName) throws OperationException {
        return operate(columnName1, columnName2, resultColumnName, (n1, n2) -> n1.doubleValue() / n2.doubleValue());
    }

    /**
     * Adds a new column with values obtained by multiplying corresponding elements in two columns.
     *
     * @param columnName1      the name of the first column.
     * @param columnName2      the name of the second column.
     * @param resultColumnName the name of the result column.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the columns contain non-numeric values or have different sizes.
     */
    public OperationBuilder mul(String columnName1, String columnName2, String resultColumnName) throws OperationException {
        return operate(columnName1, columnName2, resultColumnName, (n1, n2) -> n1.doubleValue() * n2.doubleValue());
    }

    /**
     * Computes the sum of numeric values in each column of the result table and appends the total as a new row.
     *
     * - For columns containing numeric values (`Number`), it calculates the sum of all rows excluding the added rows.
     * - For columns containing strings that can be converted to numbers, it attempts the conversion and calculates the sum.
     * - For non-numeric columns or invalid strings, appends "N/A" to the column.
     *
     * @return the current {@link OperationBuilder} instance for method chaining.
     */
    public OperationBuilder sum() {

        for (Column column : this.resultTable.getColumns()) {
            double total = 0;


            if (column.getRows().getFirst() instanceof Number) {
                for (int i = 0; i < column.getRows().size() - this.addedRows; i++) {
                    Number value = (Number) column.getRows().get(i);
                    total += value.doubleValue();
                }

                column.addRow((double) Math.round(total * 100) / 100);
            }

            // Check if the column contains Strings that can be converted to numbers
            else if (column.getRows().getFirst() instanceof String && !column.getName().equals("Folder")) {
                try {
                    for (int i = 0; i < column.getRows().size() - this.addedRows; i++) {
                        double value = Double.parseDouble((String) column.getRows().get(i));
                        total += value;
                    }
                    column.addRow((double) Math.round(total * 100) / 100);
                } catch (NumberFormatException e) {
                    column.addRow("N/A");
                }
            }
            else {
                column.addRow("N/A");
            }
        }

        this.addedRows += 1;
        return this;
    }

    /**
     * Calculates the average of numeric values in each column of the result table and appends it as a new row.
     *
     * - For columns containing numeric values (`Number`), it calculates the average of all rows excluding the added rows.
     * - For columns containing strings that can be converted to numbers, it attempts the conversion and calculates the average.
     * - For non-numeric columns or invalid strings, appends "N/A" to the column.
     *
     * The calculated average for numeric columns is rounded to two decimal places.
     *
     * @return the current {@link OperationBuilder} instance for method chaining.
     */
    public OperationBuilder average() {
        for (Column column : this.resultTable.getColumns()) {
            double total = 0;
            int count = column.getRows().size() - this.addedRows;

            if (column.getRows().getFirst() instanceof Number) {
                for (int i = 0; i < count; i++) {
                    Number value = (Number) column.getRows().get(i);
                    total += value.doubleValue();
                }

                double average = (double) Math.round((total / count) * 100) / 100;
                column.addRow(average);
            }

            // Check if the column contains Strings that can be converted to numbers
            else if (column.getRows().getFirst() instanceof String && !column.getName().equals("Folder")) {
                try {
                    for (int i = 0; i < count; i++) {
                        String rowValue = (String) column.getRows().get(i);
                        double value = Double.parseDouble(rowValue);
                        total += value;
                    }

                    double average = (double) Math.round((total / count) * 100) / 100;
                    column.addRow(average);
                } catch (NumberFormatException e) {
                    column.addRow("N/A");
                }
            } else {
                column.addRow("N/A");
            }
        }

        this.addedRows += 1;
        return this;
    }

    /**
     * Removes a column from the table.
     *
     * @param columnName the name of the column to remove.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the column does not exist.
     */
    public OperationBuilder remove(String columnName) throws OperationException {
        this.checkColumn(columnName);
        this.resultTable.remove(columnName);
        return this;
    }

    /**
     * Renames a column in the table.
     *
     * @param columnOldName the current name of the column.
     * @param columnNewName the new name for the column.
     * @return the current instance of {@code OperationBuilder}.
     * @throws OperationException if the column does not exist.
     */
    public OperationBuilder rename(String columnOldName, String columnNewName) throws OperationException {
        this.checkColumn(columnOldName);
        this.resultTable.renameColumn(columnOldName, columnNewName);
        return this;
    }

    /**
     * Adds a suffix to the specified column name in the result table.
     *
     * @param suffix the suffix to be appended to the column name.
     * @param columnName the name of the column to which the suffix will be added.
     * @return the current instance of {@link OperationBuilder} for method chaining.
     * @throws OperationException if the column cannot be renamed or does not exist.
     */
    public OperationBuilder addSuffix(String suffix, String columnName) throws OperationException {
        this.resultTable.renameColumn(columnName, columnName + " " + suffix);
        return this;
    }

    /**
     * Adds a prefix to the specified column name in the result table.
     *
     * @param prefix the prefix to be prepended to the column name.
     * @param columnName the name of the column to which the prefix will be added.
     * @return the current instance of {@link OperationBuilder} for method chaining.
     * @throws OperationException if the column cannot be renamed or does not exist.
     */
    public OperationBuilder addPrefix(String prefix, String columnName) throws OperationException {
        this.resultTable.renameColumn(columnName, prefix + " " + columnName);
        return this;
    }

    /**
     * Merges columns from an imported table into the result table.
     *
     * @param importedTable the {@link Table} instance to merge.
     */
    public void mergeTables(Table importedTable) {
        this.resultTable.appendColumns(importedTable.getColumns());
    }

    /**
     * Retrieves the current result table.
     *
     * @return the current {@link Table} instance.
     */
    public Table getTable() {
        return this.resultTable;
    }

    /**
     * Sets the current result table to the specified table.
     *
     * @param table the {@link Table} instance to set as the result table.
     */
    public void setTable(Table table){
        this.resultTable = table;
    }

    /**
     * Completes the operation and returns the associated {@link TableBuilder} for further table modifications.
     *
     * @return the {@link TableBuilder} instance linked to this operation.
     */
    public TableBuilder end() {
        this.tableBuilder.setTable(this.resultTable);
        return this.tableBuilder;
    }
}
