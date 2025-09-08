package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import model.User;
import model.UserDAO;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        signUpButton.setOnAction(e -> openSignUp());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        User user = UserDAO.loginUser(username, password);
        if (user != null) {
            openMainDashboard(user);
        } else {
            messageLabel.setText("Invalid username or password!");
        }
    }

    private void openSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sign Up - CashFlow Analyzer");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainDashboard.fxml"));
            System.out.println(getClass().getResource("/fxml/MainDashboard.fxml"));

            Parent root = loader.load();

            // Pass current user to the controller
            MainController controller = loader.getController();
            controller.setCurrentUser(user);

            // Replace the current stage's scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("CashFlow Analyzer - Dashboard");
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to open MainDashboard.fxml!");
        }
    }

}
