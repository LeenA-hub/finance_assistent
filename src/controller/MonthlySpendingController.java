package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Transaction;
import model.TransactionDAO;
import model.User;

import java.util.*;

public class MonthlySpendingController {

    @FXML
    private BarChart<String, Number> spendingChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox<String> intervalBox;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadChart("Monthly"); // default
    }

    @FXML
    public void initialize() {
        if (intervalBox != null) {
            intervalBox.setOnAction(e -> loadChart(intervalBox.getValue()));
        }
    }

    private void loadChart(String interval) {
        if (currentUser == null)
            return;

        List<Transaction> transactions = TransactionDAO.getTransactionsByUser(currentUser.getId());
        Map<String, Double> dataMap = new TreeMap<>();

        for (Transaction t : transactions) {
            String key;
            if ("Biweekly".equals(interval)) {
                key = getBiweeklyPeriod(t.getDate());
            } else {
                key = t.getDate().substring(0, 7); // monthly YYYY-MM
            }
            dataMap.put(key, dataMap.getOrDefault(key, 0.0) + t.getAmount());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        dataMap.forEach((period, total) -> series.getData().add(new XYChart.Data<>(period, total)));

        spendingChart.getData().clear();
        spendingChart.getData().add(series);
    }

    /** Returns a string like "YYYY-MM Biweek1" or "YYYY-MM Biweek2" */
    private String getBiweeklyPeriod(String date) {
        String month = date.substring(0, 7); // YYYY-MM
        int day = Integer.parseInt(date.substring(8, 10));
        return month + (day <= 15 ? " Biweek1" : " Biweek2");
    }
}
