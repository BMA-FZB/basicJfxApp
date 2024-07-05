package mbouch.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBase {

    private static DataBase instance;
    private static final Logger logger = Logger.getLogger(DataBase.class.getName());
    private Connection connection;
    private String serverUrl = "jdbc:mysql://localhost:3306/";
    private String databaseName = "test";
    private String username = "root";
    private String password = "jgCmDuqz5#";
    //private String initSqlPath = "src/main/java/mbouch/database/init.sql"; // Adjust the path to your init.sql file
    private String initSqlPath = "src/main/resources/mbouch/database/init.sql";

    // Private constructor to prevent instantiation
    private DataBase() {
        try {
            // Connect to the MySQL server first (without specifying the database)
            Connection serverConnection = DriverManager.getConnection(serverUrl, username, password);

            // Execute the init.sql script
            boolean success = executeInitScript(serverConnection, initSqlPath);
            if (!success) {
                logger.log(Level.SEVERE, "Failed to execute init script");
                return;
            }

            // Now connect to the specific database
            connection = DriverManager.getConnection(serverUrl + databaseName, username, password);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DataBase connection error: ", e);
        }
    }

    // Singleton instance method
    public static synchronized DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    // Method to execute the init.sql script
// Method to execute the init.sql script
    private boolean executeInitScript(Connection serverConnection, String sqlFilePath) {
        try {
            List<String> sqlStatements = parseSQLScript(sqlFilePath);

            if (sqlStatements.isEmpty()) {
                return false;
            }

            // Execute CREATE DATABASE statement if present
            if (sqlStatements.get(0).toUpperCase().startsWith("CREATE DATABASE")) {
                try (Statement statement = serverConnection.createStatement()) {
                    statement.execute(sqlStatements.get(0));
                    sqlStatements.remove(0);
                }
            }

            // Connect to the specific database after it's created
            try (Connection dbConnection = DriverManager.getConnection(serverUrl + databaseName, username, password)) {
                executeSQLBatches(dbConnection, sqlStatements, 10);
            }

            return true;
        } catch (IOException | SQLException e) {
            logger.log(Level.SEVERE, "Error executing init script: ", e);
            return false;
        }
    }

    // Method to parse the SQL script into individual statements
    private List<String> parseSQLScript(String scriptFilePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        Pattern commentPattern = Pattern.compile("--.*|/\\*(.|[\\r\\n])*?\\*/");

        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {
            StringBuilder currentStatement = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher commentMatcher = commentPattern.matcher(line);
                line = commentMatcher.replaceAll("").trim();

                if (line.isEmpty()) {
                    continue;
                }

                currentStatement.append(line).append(" ");

                if (line.endsWith(";")) {
                    sqlStatements.add(currentStatement.toString().trim());
                    currentStatement.setLength(0);
                }
            }
        }
        return sqlStatements;
    }
    // Method to execute SQL statements in batches
    private void executeSQLBatches(Connection connection, List<String> sqlStatements, int batchSize) throws SQLException {
        int count = 0;
        connection.setAutoCommit(false); // Disable auto-commit
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlStatements) {
                statement.addBatch(sql);
                count++;

                if (count % batchSize == 0) {
                    statement.executeBatch();
                    statement.clearBatch();
                }
            }

            if (count % batchSize != 0) {
                statement.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true); // Re-enable auto-commit
        }
    }

    // Method to execute a query
    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Query execution error: ", e);
        }
        return resultSet;
    }

    // Method to execute an update
    public int executeUpdate(String query) {
        int result = 0;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Update execution error: ", e);
        }
        return result;
    }

    // Method to execute a prepared statement query
    public ResultSet executePreparedStatementQuery(String query, Object... parameters) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Prepared statement query execution error: ", e);
        }
        return resultSet;
    }

    // Method to execute a prepared statement update
    public int executePreparedStatementUpdate(String query, Object... parameters) {
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Prepared statement update execution error: ", e);
        }
        return result;
    }

    // Method to check if a table exists
    public boolean tableExists(String tableName) {
        boolean exists = false;
        try {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null);
            exists = resultSet.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if table exists: ", e);
        }
        return exists;
    }

    // Method to close the connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing connection: ", e);
            }
        }
    }
}
