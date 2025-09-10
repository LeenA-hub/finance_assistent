
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import model.Transaction;
import model.TransactionDAO;
import model.User;
import util.Database;

import java.io.PrintWriter;
import java.util.*;
import java.io.IOException;

public class MainController {

    @FXML
    private TextField dateField, categoryField, amountField, descriptionField, incomeField;
    @FXML
    private TableView<Transaction> tableView;
    @FXML
    private TableColumn<Transaction, String> colDate, colCategory, colAmount, colDescription;
    @FXML
    private PieChart categoryChart;
    @FXML
    private BarChart<String, Number> monthlyChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label balanceLabel, savingsLabel, welcomeLabel;
    @FXML
    private Button logoutButton, setIncomeButton, spendingDistButton, analysisButton;

    private User currentUser;
    private double income = 0.0;

    /** Called from LoginController after login */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        }
        loadTransactions();
    }

    @FXML
    public void initialize() {
        System.out.println("MainController initialize called!");
        Database.initialize(); // ensure tables exist

        // Table columns
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colAmount.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAmount())));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        if (logoutButton != null)
            logoutButton.setOnAction(e -> handleLogout());
        if (setIncomeButton != null)
            setIncomeButton.setOnAction(e -> handleSetIncome());
        if (spendingDistButton != null)
            spendingDistButton.setOnAction(e -> handleSpendingDist());
        if (analysisButton != null)
            analysisButton.setOnAction(e -> handleAnalysis());
    }

    /** Add transaction for the current user */
    @FXML
    public void handleAdd() {
        if (currentUser == null)
            return;
        }

    try

    {
        Transaction t = new Transaction(
                dateField.getText(),
                categoryField.getText(),
                Double.parseDouble(amountField.getText()),
                descriptionField.getText());

        TransactionDAO.addTransactionForUser(t, currentUser.getId());
        refreshTransactions();

        dateField.clear();
        categoryField.clear();
        amountField.clear();
        descriptionField.clear();
    }catch(
    NumberFormatException e)
    {
        showAlert("Invalid Input", "Amount must be a number.");
    }catch(
    Exception e)
    {
        e.printStackTrace();
        showAlert("Error", "Failed to add transaction.");
    }
    }

    /** Save user income */
    @FXML
    public void handleSetIncome() {
        try {
            income = Double.parseDouble(incomeField.getText());
            refreshTransactions(); // update savings instantly
            showAlert("Income Updated", "Income set to $" + income);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Income must be a number.");
        }
    }

    /** Export transactions to CSV */
    @FXML
    public void handleExportCSV() {
        if (currentUser == null)
            return;
        try (PrintWriter writer = new PrintWriter("transactions.csv")) {
            writer.println("Date,Category,Amount,Description");
            for (Transaction t : TransactionDAO.getTransactionsByUser(currentUser.getId())) {
                writer.printf("%s,%s,%.2f,%s\n", t.getDate(), t.getCategory(), t.getAmount(), t.getDescription());
            }
            showAlert("Export Success", "Transactions exported to transactions.csv");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Export Failed", "Could not export CSV.");
        }
    }

    /** Logout to Login screen */
    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("CashFlow Analyzer - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Login screen.");
        }
    }

    /** Load transactions from database */
    public void loadTransactions() {
        if (currentUser == null)
            return;
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionsByUser(currentUser.getId());
            if (transactions == null)
                transactions = new ArrayList<>();
            refreshTransactions(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load transactions.");
        }
    }

    private void refreshTransactions() {
        refreshTransactions(TransactionDAO.getTransactionsByUser(currentUser.getId()));
    }

    private void refreshTransactions(List<Transaction> transactions) {
        ObservableList<Transaction> userTransactions = FXCollections.observableArrayList(transactions);
        tableView.setItems(userTransactions);
        updateDashboard(userTransactions);
    }

    private void updateDashboard(ObservableList<Transaction> transactions) {
        updateBalance(transactions);
        updateCategoryChart(transactions);
        updateMonthlyChart(transactions);
    }

    private void updateBalance(ObservableList<Transaction> transactions) {
        double totalSpent = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        balanceLabel.setText(String.format("Total Spending: $%.2f", totalSpent));
        double savings = income - totalSpent;
        savingsLabel.setText(String.format("Savings: $%.2f", savings));
    }

    private void updateCategoryChart(ObservableList<Transaction> transactions) {
        Map<String, Double> map = new HashMap<>();
        for (Transaction t : transactions) {
            map.put(t.getCategory(), map.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        map.forEach((k, v) -> pieData.add(new PieChart.Data(k, v)));
        categoryChart.setData(pieData);
    }

    private void updateMonthlyChart(ObservableList<Transaction> transactions) {
        Map<String, Double> monthlyMap = new TreeMap<>();
        for (Transaction t : transactions) {
            if (t.getDate().length() >= 7) {
                String month = t.getDate().substring(0, 7);
                monthlyMap.put(month, monthlyMap.getOrDefault(month, 0.0) + t.getAmount());
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        monthlyMap.forEach((month, total) -> series.getData().add(new XYChart.Data<>(month, total)));

        monthlyChart.getData().clear();
        monthlyChart.getData().add(series);
    }

    /** Open Monthly/Biweekly Spending page */
    @FXML
    private void handleSpendingDist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monthly_spending.fxml"));
            Parent root = loader.load();

            // Pass currentUser to the new controller
            controller.MonthlySpendingController msController = loader.getController();
            msController.setCurrentUser(currentUser);

            Scene scene = new Scene(root, 1000, 700);
            Stage stage = (Stage) spendingDistButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Monthly Spending page.");
        }
    }

    /** Open Analysis page */
    @FXML
    private void handleAnalysis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/analysis.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) analysisButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Analysis page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
