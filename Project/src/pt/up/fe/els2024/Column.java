package pt.up.fe.els2024;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Column} represents a column in a table structure. Each column has a name and a list of rows,
 * where each row can store an object (which can be data of various types).
 * <p>
 * This class provides methods to manipulate the column's name and rows. It also allows for adding new
 * rows, accessing rows by index, and retrieving the name of the column.
 * </p>
 */
public class Column {

    private String name;
    private List<Object> rows;

    /**
     * Default constructor that initializes a column with no name and an empty list of rows.
     */
    public Column() {
        this.name = null;
        this.rows = new ArrayList<>();
    }

    /**
     * Constructor to initialize a column with a specific name.
     * The rows list will be empty upon creation.
     *
     * @param name the name of the column
     */
    public Column(String name) {
        this.name = name;
        this.rows = new ArrayList<>();
    }

    /**
     * Constructor to initialize a column with a specific name and a given list of rows.
     *
     * @param name the name of the column
     * @param rows the list of rows to initialize the column with
     */
    public Column(String name, List<Object> rows) {
        this.name = name;
        this.rows = rows;
    }

    /**
     * Retrieves the name of the column.
     *
     * @return the name of the column
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a new name for the column.
     *
     * @param newName the new name to set for the column
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Retrieves the list of rows in the column.
     *
     * @return a list of rows in the column
     */
    public List<Object> getRows() {
        return this.rows;
    }

    public void setRows(List<Object> rows) {
        this.rows = rows;
    }

    /**
     * Adds a new row to the column.
     *
     * @param row the row to add to the column
     */
    public void addRow(Object row) {
        this.rows.add(row);
    }

    /**
     * Retrieves the value at a specific index in the column's rows.
     * Returns {@code null} if the index is out of bounds.
     *
     * @param index the index of the row to retrieve
     * @return the value of the row at the specified index, or {@code null} if the index is out of bounds
     */
    public Object getValueAttribute(int index) {
        return index < this.rows.size() ? this.rows.get(index) : null;
    }
}
