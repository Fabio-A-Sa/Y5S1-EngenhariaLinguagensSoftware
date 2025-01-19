package pt.up.fe.els2024.parser;

import com.google.inject.Injector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.xtext.example.mydsl.PreqlStandaloneSetup;
import org.xtext.example.mydsl.preql.*;
import org.xtext.example.mydsl.preql.impl.*;
import pt.up.fe.els2024.builders.Builder;
import pt.up.fe.els2024.builders.ImportBuilder;
import pt.up.fe.els2024.builders.OperationBuilder;
import pt.up.fe.els2024.builders.SelectBuilder;
import pt.up.fe.els2024.builders.TableBuilder;
import pt.up.fe.els2024.exception.DSLException;
import pt.up.fe.els2024.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for parsing a custom Domain-Specific Language (DSL) PREQL and translating
 * the parsed structure into a series of builder operations to process data tables.
 * The parser is designed to handle various DSL constructs such as table creation, 
 * data import, column and row operations, and output configurations.
 */
public class DSLParser {

    /** 
     * Counter used to generate unique names for operations during the parsing process. 
     */
    private int operationCounter;

    /**
     * A flag indicating whether the parser is in "extension mode," which determines 
     * if additional extension-specific operations are being processed.
     */
    private boolean whenExtensionMode;

    /**
     * Initializes the DSLParser and configures the EMF environment with dependency injection.
     */
    public DSLParser() {
        Injector injector = new PreqlStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);

        this.operationCounter = 0;
        this.whenExtensionMode = false;
    }

    /**
     * Resets the builder chain until the specified target class is reached.
     *
     * @param builder    The current builder in the chain.
     * @param targetClass The class type to reset the builder to.
     * @return The builder adjusted to the specified target class.
     * @throws Exception If resetting fails due to an invalid chain.
     */
    private Builder reset(Builder builder, Class<?> targetClass) throws Exception {
        while (!targetClass.isInstance(builder)) {
            builder = builder.end();
        }
        return builder;
    }

    /**
     * Retrieves the file extension as a string based on the specified EXT_TYPE.
     *
     * @param type The EXT_TYPE enumeration value.
     * @return A string representing the file extension, or null if not applicable.
     */
    private String getExtension(EXT_TYPE type) {
        return switch(type) {
            case NULL -> null;
            case YAML -> "yaml";
            case XML -> "xml";
            case JSON -> "json";
        };
    }

    /**
     * Retrieves the constraint type as a string based on the specified COLUMN_FILTER.
     *
     * @param type The COLUMN_FILTER enumeration value.
     * @return A string representing the constraint type, or null if not applicable.
     */
    private String getConstraint(COLUMN_FILTER type) {
        return switch (type) {
            case NULL -> null;
            case COMPOSITE -> "COMPOSITE";
            case NON_COMPOSITE -> "NON-COMPOSITE";
        };
    }

    /**
     * Determines the column position based on the specified COLUMN_POSITION.
     *
     * @param type The COLUMN_POSITION enumeration value.
     * @return True if the column should be positioned first, false otherwise.
     */
    private boolean getColumnPosition(COLUMN_POSITION type) {
        return switch (type) {
            case NULL, LAST -> false;
            case FIRST -> true;
        };
    }

    /**
     * Retrieves the function name as a string based on the specified FUNCTION type.
     *
     * @param type The FUNCTION enumeration value.
     * @return A string representing the function name, or null if not applicable.
     */
    private String getFunctionName(FUNCTION type) {
        return switch(type) {
            case NULL -> null;
            case MAX -> "MAX";
            case MIN -> "MIN";
        };
    }

    /**
     * Generates a unique operation name using an internal counter.
     *
     * @return A string representing the operation name.
     */
    private String getOperationName() {
        this.operationCounter += 1;
        return "operation" + this.operationCounter;
    }

    /**
     * Parses a CreateTableImpl node and updates the builder with the corresponding table definition.
     *
     * @param createTable The CreateTableImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the table definition.
     * @throws Exception If the parsing fails.
     */
    private Builder createTableParser(CreateTableImpl createTable, Builder builder) throws Exception {
        builder = this.reset(builder, TableBuilder.class);
        String tableName = Utils.stripQuotes(createTable.getName());
        return ((TableBuilder) builder).withName(tableName);
    }

    /**
     * Parses an OutputTableImpl node and updates the builder with the output table path.
     *
     * @param outputTable The OutputTableImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the output table path.
     * @throws Exception If the parsing fails.
     */
    private Builder outputTableParser(OutputTableImpl outputTable, Builder builder) throws Exception {
        builder = this.reset(builder, TableBuilder.class);
        String path = Utils.stripQuotes(outputTable.getOutputPath());
        ((TableBuilder) builder).assemble(path);
        return builder;
    }

    /**
     * Parses an ImportDataFolderImpl node and updates the builder with the folder import details.
     *
     * @param importDataFolder The ImportDataFolderImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the folder import details.
     * @throws Exception If the parsing fails.
     */
    private Builder importDataFolderParser(ImportDataFolderImpl importDataFolder, Builder builder) throws Exception {
        builder = this.reset(builder, TableBuilder.class);
        String source = Utils.stripQuotes(importDataFolder.getSourcePath());
        String extension = this.getExtension(importDataFolder.getExtType());
        String operationName = this.getOperationName();
        return ((TableBuilder) builder).performOperation(operationName)
                                       .withImport()
                                       .fromFolder(source, extension);
    }

    /**
     * Parses an ImportDataFoldersImpl node and updates the builder with multiple folder imports.
     *
     * @param importDataFolders The ImportDataFoldersImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the multiple folder imports.
     * @throws Exception If the parsing fails.
     */
    private Builder importDataFoldersParser(ImportDataFoldersImpl importDataFolders, Builder builder) throws Exception {
        builder = this.reset(builder, TableBuilder.class);
        String source = Utils.stripQuotes(importDataFolders.getSourcePath());
        String operationName = this.getOperationName();
        return ((TableBuilder) builder).performOperation(operationName)
                                       .withImport()
                                       .fromFolders(source);
    }

    /**
     * Parses an ImportDataFileImpl node and updates the builder with the file import details.
     *
     * @param importDataFile The ImportDataFileImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the file import details.
     * @throws Exception If the parsing fails.
     */
    private Builder importDataFileParser(ImportDataFileImpl importDataFromFile, Builder builder) throws Exception {

        builder = this.reset(builder, TableBuilder.class);
        String source = Utils.stripQuotes(importDataFromFile.getSourcePath());
        String operationName = this.getOperationName();
        return ((TableBuilder) builder).performOperation(operationName)
                                       .withImport()
                                       .fromFile(source);
    }

    /**
     * Parses a SelectionColumnImpl node and updates the builder with the column selection details.
     *
     * @param selectionColumn The SelectionColumnImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the column selection details.
     * @throws Exception If the parsing fails.
     */
    private Builder selectionColumnParser(SelectionColumnImpl selectionColumn, Builder builder) throws Exception {
        builder = this.reset(builder, ImportBuilder.class);
        String columnName = Utils.stripQuotes(selectionColumn.getColumnName());
        String constraint = this.getConstraint(selectionColumn.getColumnFilter());
        List<String> filters = constraint == null ? null : List.of(constraint);
        return ((ImportBuilder) builder).selectByColumn(columnName, filters);
    }

    /**
     * Parses an AddColumnNested node and updates the builder with the nested column addition details.
     *
     * @param addColumn The AddColumnNested object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the nested column addition details.
     * @throws Exception If the parsing fails.
     */
    private Builder addNestedColumnParser(AddColumnNested addColumn, Builder builder) throws Exception {
        builder = this.reset(builder, SelectBuilder.class);  
        String columnName = Utils.stripQuotes(addColumn.getColumnName());
        String columnType = addColumn.getColumnType();
        boolean insertAtStart = this.getColumnPosition(addColumn.getColumnPosition());
        return ((SelectBuilder) builder).addColumn(columnName, columnType, insertAtStart);
    }

    /**
     * Parses an AddColumnGlobal node and updates the builder with the global column addition details.
     *
     * @param addColumn The AddColumnGlobal object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the global column addition details.
     * @throws Exception If the parsing fails.
     */
    private Builder addGlobalColumnParser(AddColumnGlobal addColumn, Builder builder) throws Exception {
        builder = this.reset(builder, TableBuilder.class);
        String columnName = Utils.stripQuotes(addColumn.getColumnName());
        String columnType = addColumn.getColumnType();
        String operationName = this.getOperationName();
        boolean insertAtStart = this.getColumnPosition(addColumn.getColumnPosition());
        return ((TableBuilder) builder).performOperation(operationName).addColumn(columnName, columnType, insertAtStart);
    }

    /**
     * Parses an ExtensionImpl node and updates the builder with extension-based filtering.
     *
     * @param extension The ExtensionImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with extension-based filtering.
     * @throws Exception If the parsing fails.
     */
    private Builder extensionParser(ExtensionImpl extension, Builder builder) throws Exception {

        builder = (this.whenExtensionMode && (builder instanceof SelectBuilder))
                    ? ((ImportBuilder) builder.end()).endWhen()
                    : this.reset(builder, ImportBuilder.class);

        this.whenExtensionMode = true;

        String extensionName = Utils.stripQuotes(this.getExtension(extension.getType()));
        return ((ImportBuilder) builder).whenExtension(extensionName);
    }

    /**
     * Parses a SelectionTableImpl node and updates the builder with the table selection details.
     *
     * @param selectionTable The SelectionTableImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the table selection details.
     * @throws Exception If the parsing fails.
     */
    private Builder selectionTableParser(SelectionTableImpl selectionTable, Builder builder) throws Exception {
        builder = this.reset(builder, ImportBuilder.class);
        String table = Utils.stripQuotes(selectionTable.getTableName());
        String tableName = table.equals("*") ? null : table;
        String constraint = this.getConstraint(selectionTable.getColumnFilter());
        List<String> filters = constraint == null ? null : List.of(constraint);
        return ((ImportBuilder) builder).selectByTable(tableName, filters);
    }

    /**
     * Parses a FilterImpl node and updates the builder with filtering details.
     *
     * @param filter The FilterImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with filtering details.
     * @throws Exception If the parsing fails.
     */
    private Builder filterParser(FilterImpl filter, Builder builder) throws Exception {
        builder = this.reset(builder, ImportBuilder.class);
        String tableName = Utils.stripQuotes(filter.getTargetPath());
        String function = this.getFunctionName(filter.getFunction());
        List<String> cols = filter.getColumns();
        List<String> sanitizedCols = Utils.extractSanitizedList(cols);

        int number = filter.getTargetN();

        return number == 1 
                ? ((ImportBuilder) builder).selectByFilter(tableName, function, sanitizedCols)
                : ((ImportBuilder) builder).selectNByFilter(tableName, function, sanitizedCols, number);
    }

    /**
     * Parses a DefineSuffixImpl node and updates the builder with suffix definition details.
     *
     * @param suffix The DefineSuffixImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with suffix definition details.
     * @throws Exception If the parsing fails.
     */
    private Builder suffixParser(DefineSuffixImpl suffix, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);
        String suffixString = Utils.stripQuotes(suffix.getSuffix());
        String columnName = Utils.stripQuotes(suffix.getAppliesTo());
        return ((OperationBuilder) builder).addSuffix(suffixString, columnName);
    }

    /**
     * Parses a DefinePrefixImpl node and updates the builder with prefix definition details.
     *
     * @param suffix The DefinePrefixImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with prefix definition details.
     * @throws Exception If the parsing fails.
     */
    private Builder prefixParser(DefinePrefixImpl suffix, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);
        String prefixString = Utils.stripQuotes(suffix.getPrefix());
        String columnName = Utils.stripQuotes(suffix.getAppliesTo());
        return ((OperationBuilder) builder).addPrefix(prefixString, columnName);
    }

    /**
     * Parses a RowOperationImpl node and updates the builder with row operation details.
     *
     * @param rowOperation The RowOperationImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with row operation details.
     * @throws Exception If the parsing fails.
     */
    private Builder rowOperationParser(RowOperationImpl rowOperation, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);
        String operation = rowOperation.getRowOperation();

        return operation.contains("SUM")
                ? ((OperationBuilder) builder).sum()
                : ((OperationBuilder) builder).average();
    }

    /**
     * Parses a RemoveColumnImpl node and updates the builder to remove a column.
     *
     * @param removeColumn The RemoveColumnImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the column removed.
     * @throws Exception If the parsing fails.
     */
    private Builder removeColumnOperationParser(RemoveColumnImpl removeColumn, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);
        String colName = removeColumn.getColumnName();
        return ((OperationBuilder) builder).remove(colName);
    }

    /**
     * Parses a RenameColumnImpl node and updates the builder to rename a column.
     *
     * @param renameColumn The RenameColumnImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with the column renamed.
     * @throws Exception If the parsing fails.
     */
    private Builder renameColumnOperationParser(RenameColumnImpl renameColumn, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);
        String oldColumnName = renameColumn.getColumnName();
        String newColumnName = renameColumn.getNewColumnName();
        return ((OperationBuilder) builder).rename(oldColumnName, newColumnName);
    }

    /**
     * Parses an OperationAtRowsImpl node and updates the builder with row-based operations.
     *
     * @param operation The OperationAtRowsImpl object to parse.
     * @param builder The current builder in the chain.
     * @return The updated builder with row-based operations.
     * @throws Exception If the parsing fails.
     */
    private Builder operationAtRowsParser(OperationAtRowsImpl operation, Builder builder) throws Exception {
        builder = this.reset(builder, OperationBuilder.class);

        String column1 = operation.getColumn1();
        String column2 = operation.getColumn2();
        String resultColumn = operation.getColumnFinal();

        return switch (operation.getOperationType()) {
            case DIV -> ((OperationBuilder) builder).div(column1, column2, resultColumn);
            case MUL -> ((OperationBuilder) builder).mul(column1, column2, resultColumn);
            case SUB -> ((OperationBuilder) builder).sub(column1, column2, resultColumn);
            case SUM -> ((OperationBuilder) builder).sum(column1, column2, resultColumn);
        };
    }

    /**
     * Parses the given DSL file and applies the instructions to the builder chain.
     *
     * @param filePath The path to the DSL file to parse.
     * @throws Exception If parsing fails due to syntax errors, invalid builder operations, or unimplemented nodes.
     */
    public void parse(String filePath) throws Exception {

        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createFileURI(filePath));
        try {
            resource.load(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var errors = resource.getErrors();
        if (!errors.isEmpty()) {
            String message = "There were errors:\n" + errors.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n- ", "- ", "\n"));
            throw new RuntimeException(message);
        }

        var treeIterator = resource.getAllContents();
        Builder builder = new TableBuilder();

        while (treeIterator.hasNext()) {

            EObject element = treeIterator.next();
            
            try {

                builder = switch (element) {
                    case ModelImpl ignored -> builder;
                    case CreateTableImpl createTable -> this.createTableParser(createTable, builder);
                    case ImportDataFolderImpl importDataFolder -> this.importDataFolderParser(importDataFolder, builder);
                    case ImportDataFoldersImpl importDataFolders -> this.importDataFoldersParser(importDataFolders, builder);
                    case ImportDataFileImpl importDataFile -> this.importDataFileParser(importDataFile, builder);
                    case ExtensionImpl extension -> this.extensionParser(extension, builder);
                    case OutputTableImpl outputTable -> this.outputTableParser(outputTable, builder);
                    case SelectionColumnImpl selectionColumn -> this.selectionColumnParser(selectionColumn, builder);
                    case AddColumnGlobalImpl addColumnGlobal -> this.addGlobalColumnParser(addColumnGlobal, builder);
                    case AddColumnNestedImpl addColumnNested -> this.addNestedColumnParser(addColumnNested, builder);
                    case SelectionTableImpl selectionTable -> this.selectionTableParser(selectionTable, builder);
                    case FilterImpl filter -> this.filterParser(filter, builder);
                    case DefineSuffixImpl suffix -> this.suffixParser(suffix, builder);
                    case DefinePrefixImpl prefix -> this.prefixParser(prefix, builder);
                    case RowOperationImpl rowOperation -> this.rowOperationParser(rowOperation, builder);
                    case RemoveColumnImpl removeColumn -> this.removeColumnOperationParser(removeColumn, builder);
                    case RenameColumnImpl renameColumn -> this.renameColumnOperationParser(renameColumn, builder);
                    case OperationAtRowsImpl operation -> this.operationAtRowsParser(operation, builder);
                    default -> throw new DSLException("Node not implemented: " + element);
                };

            } catch (Exception exception) {
                throw new DSLException(exception.getMessage());
            }
        }
    }
}
