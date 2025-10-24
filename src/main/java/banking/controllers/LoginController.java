package bankingsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import bankingsystem.model.Main;
import bankingsystem.database.UserDAO;
import bankingsystem.models.UserSession;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button clerkLoginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = passwordField.getText();

        if (userId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both User ID and Password");
            return;
        }

        try {
            if (userDAO.authenticateUser(userId, password)) {
                UserSession session = UserSession.getInstance();
                
                if (session.isCustomer()) {
                    Main.showCustomerDashboard();
                } else if (session.isClerk() || session.isAdmin()) {
                    Main.showBankClerkDashboard();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unknown user role");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID or Password");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClerkLogin() {
        // Clerk-specific login
        userIdField.setText("Admin");
        passwordField.setText("Admin123");
        handleLogin();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}