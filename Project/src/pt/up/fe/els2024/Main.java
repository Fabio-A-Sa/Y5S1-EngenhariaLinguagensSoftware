package pt.up.fe.els2024;

import java.io.File;

import pt.up.fe.els2024.parser.DSLParser;

public class Main {

    /**
     * The main entry point for testing the DSLParser functionality.
     * This method parses DSL files whose directory path is provided as a command-line argument.
     *
     * @param args Command-line arguments. The first argument should be the directory containing the DSL files to parse.
     * @throws Exception if any error occurs during the parsing process or if the argument is missing/invalid.
     */
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("Please provide the file path to the DSL file as a command-line argument.");
        }

        String filePath = args[0];
        File dslFile = new File(filePath);
        if (!dslFile.exists() || !dslFile.isFile() || !dslFile.getName().endsWith(".preql")) {
            throw new IllegalArgumentException("The provided path does not point to a valid file: " + filePath);
        }

        // Executing preql file
        DSLParser parser = new DSLParser();
        parser.parse(filePath);
    
        /* Internal DSL for Alice scenario

        Table aliceTable = new TableBuilder()

                                    .withName("Alice Table")

                                    .performOperation("Op1")
                                        .withImport()
                                            .fromFolder("resources/assignment_1/input/", "yaml")
                                                .selectByColumn("params/criterion", null)
                                                .end()
                                            .end()
                                    .end()

                                    .performOperation("Op2")
                                        .withImport()
                                            .fromFolder("resources/assignment_1/input/", "yaml")
                                                .selectByColumn("params/splitter", null)
                                                .addColumn("File", "FILENAME")
                                                .end()
                                            .end()
                                    .end()
                                    .assemble("resources/assignment_3/output/output_internal_alice.html");
        */

        /* Internal DSL for Bob scenario

        Table bobTable = new TableBuilder()

                                    .withName("Bob Table")
    
                                    .performOperation("Op1")
                                        .withImport()
                                            .fromFile("resources/assignment_2/input/vitis-report.xml")
                                                .selectByTable("/AreaEstimates/Resources", null)
                                                .end()
                                            .end()
                                    .end()
    
                                    .performOperation("Op2")
                                        .withImport()
                                            .fromFile("resources/assignment_2/input/decision_tree.yaml")
                                                .selectByTable(null, List.of("Non-Composite"))
                                                .end()
                                            .end()
                                    .end()
    
                                    .performOperation("Op3")
                                        .withImport()
                                            .fromFile("resources/assignment_2/input/decision_tree.yaml")
                                                .selectByTable("params", null)
                                                .end()
                                            .end()
                                    .end()
    
                                    .performOperation("Op4")
                                            .withImport()
                                                .fromFile("resources/assignment_2/input/profiling.json")
                                                    .selectByFilter("/functions/time%", "MAX", List.of("name", "time%"))
                                                    .end()
                                                .end()
                                    .end()

                                    .performOperation("Op5")
                                        .addColumn("Folder", "/input/", true)
                                    .end()
                                    .assemble("resources/assignment_3/output/output_internal_bob.html");
        */ 

        /* Internal DSL for Cal scenario

        Table calTable = new TableBuilder()

                                .withName("Cal Table")

                                .performOperation("Op1")
                                    .withImport()
                                        .fromFolders("resources/assignment_3/input/") // ImportBuilder
                                            
                                            .whenExtension("yaml")
                                                .selectByTable("/total/results/dynamic", null)
                                                .end()
                                            .endWhen()

                                            .whenExtension("xml")
                                                .selectByTable("/total/results/static", null)
                                                .end()
                                            .endWhen()

                                            .whenExtension("json")
                                                .selectNByFilter("/functions/time%", "MAX", List.of("name #1", "time% #1", "name #1", "time% #1", "name #2", "time% #2", "name #3", "time% #3"), 3)
                                                .addColumn("Folder", "FOLDERNAME")
                                                .end()
                                            .endWhen()

                                        .end()

                                        .addSuffix("(Dynamic)", "iterations")
                                        .addSuffix("(Dynamic)", "calls")
                                        .addSuffix("(Static)", "nodes")
                                        .addSuffix("(Static)", "functions")

                                        .end()
                                    .end()
                                .end()

                                .performOperation("Op2")
                                    .sum()
                                    .average()
                                .end()

                            .end()
                            .assemble("resources/assignment_3/output/output_internal_cal.html");
        */
    }
}