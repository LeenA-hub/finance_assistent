
package util;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection connect() {
        try {
            // Force-load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "description TEXT" +
                ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
