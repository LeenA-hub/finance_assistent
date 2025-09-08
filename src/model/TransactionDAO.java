package model;

import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Add a transaction for a specific user
    public static void addTransactionForUser(Transaction t, int userId) {
        String sql = "INSERT INTO transactions(user_id, date, category, amount, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, t.getDate());
            pstmt.setString(3, t.getCategory());
            pstmt.setDouble(4, t.getAmount());
            pstmt.setString(5, t.getDescription());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all transactions for a specific user
    public static List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date ASC";

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id")); // Add id to transaction
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

    // Optional: delete a transaction by ID
    public static void deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Optional: update a transaction
    public static void updateTransaction(Transaction t) {
        String sql = "UPDATE transactions SET date = ?, category = ?, amount = ?, description = ? WHERE id = ?";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getDate());
            pstmt.setString(2, t.getCategory());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setString(4, t.getDescription());
            pstmt.setInt(5, t.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
