package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:cashflow.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Call this at app startup
    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        );
                    """);

            // Transactions table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS transactions (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            date TEXT,
                            category TEXT,
                            amount REAL,
                            description TEXT,
                            FOREIGN KEY(user_id) REFERENCES users(id)
                        );
                    """);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
