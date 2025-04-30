package itineraryplanner;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/itinerary_db?" +
                                    "useSSL=false&" +
                                    "allowPublicKeyRetrieval=true&" +
                                    "serverTimezone=UTC&" +
                                    "autoReconnect=true";
    
    private static final String USER = "Mishika";
    private static final String PASSWORD = "1@345678A";
    
    private static volatile Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            System.err.println("Please add MySQL Connector/J to your classpath");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Created new database connection successfully!");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Error connecting to database: " + e.getMessage());
            System.err.println("Troubleshooting steps:");
            System.err.println("1. Verify MySQL server is running");
            System.err.println("2. Check username/password are correct");
            System.err.println("3. Ensure user has proper permissions");
            System.err.println("4. Confirm database 'itinerary_db' exists");
            System.err.println("5. Check if MySQL is configured to accept connections");
            e.printStackTrace();
            return null;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("✅ Database connection closed successfully!");
                }
            } catch (SQLException e) {
                System.err.println("❌ Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    public static boolean testConnection() {
        System.out.println("\nTesting database connection...");
        System.out.println("Connection URL: " + URL);
        System.out.println("Username: " + USER);
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS test_value, DATABASE() AS db_name, USER() AS mysql_user")) {
            
            if (rs.next()) {
                System.out.println("✔ Connection test successful!");
                System.out.println("Test value: " + rs.getInt("test_value"));
                System.out.println("Database: " + rs.getString("db_name"));
                System.out.println("MySQL user: " + rs.getString("mysql_user"));
                return true;
            } else {
                System.err.println("❌ Connection test failed - no results returned");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
        if (testConnection()) {
            System.out.println("\nDatabase connection is working properly!");
        } else {
            System.out.println("\nDatabase connection failed. Please check:");
            System.out.println("1. Is MySQL server running?");
            System.out.println("2. Are the credentials correct?");
            System.out.println("3. Does the user have proper permissions?");
            System.out.println("4. Does the database 'itinerary_db' exist?");
            System.out.println("5. Is MySQL configured to accept connections?");
            System.out.println("\nTry running these commands in MySQL:");
            System.out.println("CREATE DATABASE IF NOT EXISTS itinerary_db;");
            System.out.println("CREATE USER IF NOT EXISTS 'Mishika'@'localhost' IDENTIFIED BY '1@345678A';");
            System.out.println("GRANT ALL PRIVILEGES ON itinerary_db.* TO 'Mishika'@'localhost';");
            System.out.println("FLUSH PRIVILEGES;");
        }
    }
}