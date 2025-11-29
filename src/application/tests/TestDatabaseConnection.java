package application.tests;

import application.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Test class for verifying SQL Server database connection.
 * This class attempts to establish a connection and execute a simple query.
 */
public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("=== SQL Server Connection Test ===");

        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            System.err.println("Failed to establish connection.");
            return;
        }

        System.out.println("Connection object: " + conn);

        // Test a query
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT 1 AS TestValue";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("Query OK. Returned value: " + rs.getInt("TestValue"));
            }

        } catch (SQLException e) {
            System.err.println("SQL Execution Error: " + e.getMessage());
        }

        // Close connection
        try {
            DatabaseConnection.closeConnection();
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }

        System.out.println("=== Test Completed ===");
    }
}
