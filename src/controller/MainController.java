package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.PieChart;
import model.Transaction;
import model.TransactionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.PieChart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MainController {
    @FXML
    TextField dateField, categoryField, amountField, descriptionField;
    @FXML
    TableView<Transaction> tableView;
    @FXML
    TableColumn<Transaction, String> colDate, colCategory, colAmount, colDescription;
    @FXML
    PieChart categoryChart;

    @FXML
    public void initialize() {
        tableView.setItems(FXCollections.observableArrayList(TransactionDAO.getAllTransactions()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAmount())));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        // If no transactions exist, add sample data
        if (tableView.getItems().isEmpty()) {
            Transaction sample1 = new Transaction("2025-01-01", "Food", 25.50, "Groceries");
            Transaction sample2 = new Transaction("2025-01-05", "Transport", 15.00, "Bus pass");
            tableView.getItems().addAll(sample1, sample2);

            // Also save them in DAO so chart updates
            TransactionDAO.addTransaction(sample1);
            TransactionDAO.addTransaction(sample2);
        }

        updateChart();
    }

    @FXML
    public void handleAdd() {
        Transaction t = new Transaction(dateField.getText(), categoryField.getText(),
                Double.parseDouble(amountField.getText()), descriptionField.getText());
        TransactionDAO.addTransaction(t);
        tableView.getItems().add(t);

        updateChart(); // Update chart dynamically
    }

    @FXML
    public void handleExportCSV() {
        try (PrintWriter writer = new PrintWriter("transactions.csv")) {
            writer.println("Date,Category,Amount,Description");
            for (Transaction t : TransactionDAO.getAllTransactions()) {
                writer.printf("%s,%s,%.2f,%s\n", t.getDate(), t.getCategory(), t.getAmount(), t.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChart() {
        Map<String, Double> map = new HashMap<>();
        for (Transaction t : TransactionDAO.getAllTransactions()) {
            map.put(t.getCategory(), map.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        map.forEach((k, v) -> pieData.add(new PieChart.Data(k, v)));
        categoryChart.setData(pieData);
    }
}
