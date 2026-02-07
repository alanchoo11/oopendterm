package com.sport.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class.
 * Uses basic DriverManager for JDBC connection to PostgreSQL.
 * This is the simplest approach without connection pooling.
 */
public class DBConnection {
    
    // Database configuration - in real app, use properties file
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/alans";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "ALAN2007";
    
    // JDBC Driver class name
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    
    // Static block to load driver once
    static {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("PostgreSQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load PostgreSQL JDBC Driver: " + e.getMessage());
            throw new RuntimeException("Failed to load PostgreSQL JDBC Driver", e);
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class.
     */
    private DBConnection() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Gets a connection to the database.
     * Uses DriverManager to create a new connection each time.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully");
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Closes a database connection safely.
     * 
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection.
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
