package com.nhlstenden.reader2.controllers;

import com.nhlstenden.reader2.annotations.Exclude;

import java.lang.reflect.Field;
import java.sql.*;
import java.net.URL;
import java.nio.file.Paths;
import java.io.File;
import java.net.URISyntaxException;

/**
 * The DatabaseController class manages a single database connection using the singleton pattern.
 * This ensures that only one instance of the DatabaseController exists throughout the application,
 * providing a global point of access to the database connection.
 */
public class DatabaseController {

    // Singleton instance of DatabaseController
    private static DatabaseController instance;
    // Database connection object
    private Connection connection;

    /**
     * Private constructor to prevent direct instantiation.
     * Establishes a connection to the SQLite database located in the resources folder.
     */
    private DatabaseController() {
        openConnection();
    }

    /**
     * Opens a connection to the SQLite database located in the 'resources' folder.
     * If the database file is not found, an error message is printed to the console.
     * If an SQLException or URISyntaxException occurs, the stack trace is printed.
     */
    public void openConnection() {
        try {
            URL resourceUrl = getClass().getResource("/com/nhlstenden/reader2/database/sqlite.db");
            if (resourceUrl != null) {
                File dbFile = Paths.get(resourceUrl.toURI()).toFile();
                String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                this.connection = DriverManager.getConnection(dbUrl);
            } else {
                System.err.println("Database file not found in resources folder.");
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of DatabaseController.
     * If the instance does not exist, it creates a new one.
     *
     * @return the singleton instance of DatabaseController
     */
    public static DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
        }
        return instance;
    }

    /**
     * Provides access to the established database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Executes a SQL query that returns a ResultSet, such as SELECT statements.
     * If an SQLException occurs, the stack trace is printed and null is returned.
     *
     * @param query the SQL query to be executed
     * @return the ResultSet of the executed query, or null if an error occurs
     */
    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes a SQL statement that modifies the database, such as INSERT, UPDATE, or DELETE statements.
     * If an SQLException occurs, the stack trace is printed and 0 is returned.
     *
     * @param query the SQL statement to be executed
     * @return the number of affected rows, or 0 if an error occurs
     */
    public int executeUpdate(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Closes the database connection if it is open.
     * If an SQLException occurs, the stack trace is printed.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads a model to the database.
     * The model's fields are mapped to the corresponding columns in the database table.
     * Fields annotated with @Exclude are ignored.
     * If an SQLException or IllegalAccessException occurs, the stack trace is printed and -1 is returned.
     *
     * @param <T> the type of the model
     * @param model the model to be uploaded
     * @return the generated key of the inserted row, or -1 if an error occurs
     */
    public <T> int uploadModel(T model) {

        // Open a connection to the database
        this.openConnection();

        // Get the table name from the model's class name
        String tableName = model.getClass().getSimpleName();
        // Get all declared fields of the model's class
        Field[] fields = model.getClass().getDeclaredFields();
        // StringBuilder to store column names
        StringBuilder columns = new StringBuilder();
        // StringBuilder to store values placeholders
        StringBuilder values = new StringBuilder();

        // Iterate over each field
        for (Field field : fields) {
            // Skip fields annotated with @Exclude
            if (field.isAnnotationPresent(Exclude.class)) {
                continue;
            }
            // Make the field accessible
            field.setAccessible(true);
            // Append the field name to columns
            columns.append(field.getName()).append(",");
            // Append a placeholder to values
            values.append("?,");
        }

        // Create the SQL insert statement
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                columns.substring(0, columns.length() - 1),
                values.substring(0, values.length() - 1));

        // Execute the SQL insert statement
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            // Set the values for the placeholders
            for (Field field : fields) {
                if (field.isAnnotationPresent(Exclude.class)) {
                    continue;
                }
                statement.setObject(index++, field.get(model));
            }
            // Execute the update
            statement.executeUpdate();
            // Get the generated keys
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }
}