package pt.up.fe.els2024.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@code Utils} is a utility class providing common file and path-related methods.
 * <p>
 * This class contains static methods that help manipulate file paths, file extensions,
 * and file names. It also offers methods to extract directory structures from file paths.
 * These methods are useful for various tasks involving file system operations.
 * </p>
 */
public class Utils {

    /**
     * Retrieves the file extension from a {@link File} object.
     * <p>
     * This method extracts the extension of the given file, returning it in lowercase.
     * If the file does not have an extension, it returns {@code null}.
     * </p>
     *
     * @param file the {@link File} object from which to extract the extension
     * @return the file extension in lowercase, or {@code null} if the file has no extension
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        return Utils.getExtension(fileName);
    }

    /**
     * Retrieves the file extension from a file name.
     * <p>
     * This method extracts the extension of the given file name, returning it in lowercase.
     * If the file name does not contain a dot (i.e., it has no extension), it returns {@code null}.
     * </p>
     *
     * @param fileName the name of the file to extract the extension from
     * @return the file extension in lowercase, or {@code null} if the file name has no extension
     */
    public static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Removes the file extension from a given file path.
     * <p>
     * This method strips the extension from the file path, returning the path without the file extension.
     * If the file path does not contain an extension, it returns the original path.
     * </p>
     *
     * @param filePath the file path from which to remove the extension
     * @return the file path without the extension, or the original path if no extension is present
     */
    public static String removeExtension(String filePath) {
        if (filePath == null || !filePath.contains(".")) {
            return filePath;
        }
        return filePath.substring(0, filePath.lastIndexOf("."));
    }

    /**
     * Extracts the file name (without extension) from a given file path.
     * <p>
     * This method extracts the name of the file (excluding any directory structure and extension).
     * If the file path points to a directory or the file path is empty, it returns {@code null}.
     * </p>
     *
     * @param filePath the file path from which to extract the file name
     * @return the file name without the extension, or {@code null} if the path is empty
     */
    public static String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }

        String fileNameWithExtension = filePath
                .substring(Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\')) + 1);
        return Utils.removeExtension(fileNameWithExtension);
    }

    /**
     * Returns the directory path of the given file path, excluding the file name.
     * <p>
     * This method splits the given file path and returns all directories except the last one (which
     * is assumed to be the file name).
     * </p>
     *
     * @param path the file path to process
     * @return a list of directories representing the directory path of the file, excluding the file name
     */
    public static List<String> getDirectoryPath(String path) {

        path = String.join("/", Utils.getDirectory(path).subList(0, Utils.getDirectory(path).size() - 1));
        return getDirectory(path);
    }

    /**
     * Splits the given file path into its individual directories.
     * <p>
     * This method normalizes the given path by removing any leading or trailing slashes and then splits
     * it into a list of directories. If the path is empty or consists only of slashes, it returns an empty list.
     * </p>
     *
     * @param path the file path to split into directories
     * @return a list of directories extracted from the file path
     */
    public static List<String> getDirectory(String path) {

        // null, "" or "/"
        if (path == null || path.trim().isEmpty() || path.matches("^/*$")) {
            return Collections.emptyList();
        }

        // Remove initial and end /, if available
        String normalizedPath = path.replaceAll("^/+", "").replaceAll("/+$", "");

        // Return the normalized directory path list
        String[] directories = normalizedPath.split("/");
        return new ArrayList<>(Arrays.asList(directories));
    }

    /**
     * Extracts the folder name immediately preceding the last segment in a given path.
     *
     * @param path the full file path from which to extract the folder name.
     * @return the name of the folder immediately preceding the last segment.
     * @throws IllegalArgumentException if the path does not have enough segments to extract a folder name.
     */
    public static String extractFolderName(String path) {
        List<String> directory = Utils.getDirectory(path);
        return directory.get(directory.size() - 2);
    }

    /**
     * Removes leading and trailing single or double quotes from the given string.
     *
     * @param input the input string, possibly surrounded by quotes.
     * @return the unquoted string, or {@code null} if the input is {@code null}.
     */
    public static String stripQuotes(String input) {
        return input == null ? null : input.replaceAll("^['\"]|['\"]$", "");
    }

    /**
     * Processes a list of strings, removing surrounding brackets and splitting the contents by commas.
     * Each element is sanitized by trimming whitespace and stripping quotes.
     *
     * @param list the input list of strings to sanitize.
     * @return a new list of sanitized strings.
     */
    public static List<String> extractSanitizedList(List<String> list) {
        List<String> sanitizedList = new ArrayList<>();

        for (String item : list) {

            // Remove leading and trailing brackets
            item = item.trim().replaceAll("^\\[|\\]$", "");

            // Split the string by commas and add each trimmed element
            sanitizedList.addAll(Arrays.asList(Arrays.stream(item.split(",\\s*")).map(Utils::stripQuotes).toArray(String[]::new)));
        }

        return sanitizedList;
    }
}
