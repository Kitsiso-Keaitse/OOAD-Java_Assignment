package bankingsystem.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import bankingsystem.model.Main;
import bankingsystem.database.AccountDAO;
import bankingsystem.database.TransactionDAO;
import bankingsystem.models.UserSession;
import bankingsystem.models.TransactionDTO;

import java.util.List;
import java.util.Optional;

public class CustomerDashboardController {

    @FXML private Label checkingBalance;
    @FXML private Label savingsBalance;
    @FXML private Label investmentBalance;
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> transactionsList;
    @FXML private Button makeDepositButton;
    @FXML private Button viewHistoryButton;
    @FXML private Button transferFundsButton;

    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserSession session = UserSession.getInstance();

    @FXML
    public void initialize() {
        updateWelcomeMessage();
        loadAccountBalances();
        loadRecentTransactions();
    }

    private void updateWelcomeMessage() {
        if (session.getDisplayName() != null) {
            welcomeLabel.setText("Welcome, " + session.getDisplayName());
        } else {
            welcomeLabel.setText("Welcome to Your Dashboard");
        }
    }

    @FXML
    private void showDashboard() {
        loadAccountBalances();
        loadRecentTransactions();
    }

    @FXML
    private void showAccounts() {
        // Refresh account information
        loadAccountBalances();
        showAlert(Alert.AlertType.INFORMATION, "Accounts", "Account information refreshed.");
    }

    @FXML
    private void showTransactions() {
        Main.showTransactionHistoryView();
    }

    @FXML
    private void showTransfer() {
        showTransferDialog();
    }

    @FXML
    private void handleLogout() {
        session.clearSession();
        Main.showLoginView();
    }

    @FXML
    private void handleMakeDeposit() {
        showDepositDialog();
    }

    @FXML
    private void handleViewHistory() {
        Main.showTransactionHistoryView();
    }

    @FXML
    private void handleTransferFunds() {
        showTransferDialog();
    }

    private void loadAccountBalances() {
        // In a real implementation, you would fetch these from the database
        // For now, using demo data that would come from AccountDAO
        String customerId = session.getCustomerId();
        if (customerId != null) {
            // This will be replaced with actual database calls
            // List<Account> accounts = accountDAO.getAccountsByCustomerId(customerId);
            
            // Demo balances - to be replaced with actual data
            checkingBalance.setText("BWP 1,176.01");
            savingsBalance.setText("BWP 2,423.25");
            investmentBalance.setText("BWP 6,712.27");
        }
    }

    private void loadRecentTransactions() {
        String customerId = session.getCustomerId();
        if (customerId != null) {
            List<TransactionDTO> recentTransactions = transactionDAO.getRecentTransactions(customerId, 10);
            transactionsList.getItems().clear();
            
            for (TransactionDTO transaction : recentTransactions) {
                String transactionText = String.format("%s: BWP %,.2f - %s", 
                    transaction.getDescription(), 
                    transaction.getAmount(), 
                    transaction.getTransactionDate());
                transactionsList.getItems().add(transactionText);
            }
        } else {
            // Demo transactions
            transactionsList.getItems().addAll(
                "YOUNG TOWN CINEMA: BWP 45.00 - Nov 15",
                "ATM DEPOSIT: BWP 500.00 - Nov 14", 
                "GLOBALES BIG & PUJI: BWP 120.50 - Nov 13",
                "INTEREST PAYMENT: BWP 23.15 - Nov 12",
                "LE PETITE CAFÉ: BWP 35.75 - Nov 11"
            );
        }
    }

    private void showDepositDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Make a Deposit");
        dialog.setHeaderText("Deposit funds into your account");

        // Set up form
        VBox content = new VBox(10);
        ComboBox<String> accountCombo = new ComboBox<>();
        accountCombo.setItems(FXCollections.observableArrayList("Checking", "Savings", "Investment"));
        accountCombo.setPromptText("Select Account");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");
        
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description (optional)");
        
        content.getChildren().addAll(
            new Label("Account:"), accountCombo,
            new Label("Amount:"), amountField,
            new Label("Description:"), descriptionField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processDeposit(accountCombo.getValue(), amountField.getText(), descriptionField.getText());
        }
    }

    private void showTransferDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Transfer Funds");
        dialog.setHeaderText("Transfer between your accounts");

        VBox content = new VBox(10);
        ComboBox<String> fromAccountCombo = new ComboBox<>();
        fromAccountCombo.setItems(FXCollections.observableArrayList("Checking", "Savings", "Investment"));
        fromAccountCombo.setPromptText("From Account");
        
        ComboBox<String> toAccountCombo = new ComboBox<>();
        toAccountCombo.setItems(FXCollections.observableArrayList("Checking", "Savings", "Investment"));
        toAccountCombo.setPromptText("To Account");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");
        
        content.getChildren().addAll(
            new Label("From Account:"), fromAccountCombo,
            new Label("To Account:"), toAccountCombo,
            new Label("Amount:"), amountField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processTransfer(fromAccountCombo.getValue(), toAccountCombo.getValue(), amountField.getText());
        }
    }

    private void processDeposit(String accountType, String amountStr, String description) {
        if (accountType == null || amountStr == null || amountStr.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account and enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Amount must be positive");
                return;
            }

            // save to database via AccountDAO and TransactionDAO
            if (transactionDAO.recordDeposit(session.getCustomerId(), accountType, amount, description)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    String.format("Deposited BWP %,.2f to %s account", amount, accountType));
                loadAccountBalances();
                loadRecentTransactions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to process deposit");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount");
        }
    }

    private void processTransfer(String fromAccount, String toAccount, String amountStr) {
        if (fromAccount == null || toAccount == null || amountStr == null || amountStr.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields");
            return;
        }

        if (fromAccount.equals(toAccount)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot transfer to the same account");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Amount must be positive");
                return;
            }

            //process transfer via TransactionDAO
            if (transactionDAO.processTransfer(session.getCustomerId(), fromAccount, toAccount, amount)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    String.format("Transferred BWP %,.2f from %s to %s", amount, fromAccount, toAccount));
                loadAccountBalances();
                loadRecentTransactions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Transfer failed - insufficient funds or system error");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}