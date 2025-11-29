package application.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing SQL Server database connections.
 * This class provides methods to open and close a JDBC connection
 * to a Microsoft SQL Server instance located on localhost.
 *
 * Database: QuanLyDuAn
 * Host: localhost
 * Port: 1433
 * Authentication: username/password
 */
public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QuanLyDuAn;"
            + "encrypt=false;"                // disable SSL for local
            + "trustServerCertificate=true;"; // allow self-signed certificate

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    private static Connection connection;

    /**
     * Get SQL Server connection instance.
     * Creates one if not exist or closed.
     *
     * @return Connection object or null if error occurs
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                // Load SQL Server JDBC Driver
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                // Create connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connected to SQL Server successfully.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }

        return connection;
    }

    /**
     * Close active connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
