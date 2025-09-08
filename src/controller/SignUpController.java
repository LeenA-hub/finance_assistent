package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;

public class SignUpController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        signUpButton.setOnAction(e -> handleSignUp());
        loginButton.setOnAction(e -> backToLogin());
    }

    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = UserDAO.registerUser(new User(username, password));
        if (success) {
            messageLabel.setText("Account created successfully!");
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } else {
            messageLabel.setText("Username already exists.");
        }
    }

    private void backToLogin() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}
