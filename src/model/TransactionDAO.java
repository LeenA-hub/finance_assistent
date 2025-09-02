package model;

import util.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    public static void addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions(date, category, amount, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getDate());
            pstmt.setString(2, t.getCategory());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setString(4, t.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = Database.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                t.setDate(rs.getString("date"));
                t.setCategory(rs.getString("category"));
                t.setAmount(rs.getDouble("amount"));
                t.setDescription(rs.getString("description"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
