package bankingsystem.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import bankingsystem.database.TransactionDAO;
import bankingsystem.models.UserSession;
import bankingsystem.models.TransactionDTO;

import java.time.LocalDate;
import java.util.List;

public class TransactionHistoryController {

    @FXML private ComboBox<String> accountFilterComboBox;
    @FXML private DatePicker dateFromPicker;
    @FXML private DatePicker dateToPicker;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private TableColumn<TransactionDTO, String> dateColumn;
    @FXML private TableColumn<TransactionDTO, String> descriptionColumn;
    @FXML private TableColumn<TransactionDTO, String> accountColumn;
    @FXML private TableColumn<TransactionDTO, String> typeColumn;
    @FXML private TableColumn<TransactionDTO, String> amountColumn;
    @FXML private TableColumn<TransactionDTO, String> balanceColumn;
    @FXML private Label totalDepositsLabel;
    @FXML private Label totalWithdrawalsLabel;
    @FXML private Label netChangeLabel;

    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserSession session = UserSession.getInstance();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAccountFilters();
        loadTypeFilters();
        setupDateDefaults();
        loadTransactions();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBalance"));
    }

    private void setupDateDefaults() {
        // Set default date range to last 30 days
        dateToPicker.setValue(LocalDate.now());
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
    }

    @FXML
    private void handleApplyFilters() {
        loadTransactions();
    }

    @FXML
    private void handleClearFilters() {
        accountFilterComboBox.getSelectionModel().clearSelection();
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());
        typeFilterComboBox.getSelectionModel().clearSelection();
        loadTransactions();
    }

    private void loadAccountFilters() {
        //load from database based on user's accounts
        accountFilterComboBox.setItems(FXCollections.observableArrayList(
            "All Accounts", "Checking (CHK-001)", "Savings (SAV-001)", "Investment (INV-001)"
        ));
        accountFilterComboBox.setValue("All Accounts");
    }

    private void loadTypeFilters() {
        typeFilterComboBox.setItems(FXCollections.observableArrayList(
            "All Types", "Deposit", "Withdrawal", "Transfer", "Interest"
        ));
        typeFilterComboBox.setValue("All Types");
    }

    private void loadTransactions() {
        try {
            String customerId = session.getCustomerId();
            String accountFilter = accountFilterComboBox.getValue();
            LocalDate fromDate = dateFromPicker.getValue();
            LocalDate toDate = dateToPicker.getValue();
            String typeFilter = typeFilterComboBox.getValue();

            // Remove "All Accounts" and "All Types" for database query
            String accountNumber = "All Accounts".equals(accountFilter) ? null : accountFilter;
            String transactionType = "All Types".equals(typeFilter) ? null : typeFilter;

            List<TransactionDTO> transactions = transactionDAO.getTransactionsByFilters(
                customerId, accountNumber, fromDate, toDate, transactionType);

            transactionsTable.setItems(FXCollections.observableArrayList(transactions));
            calculateSummary(transactions);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateSummary(List<TransactionDTO> transactions) {
        double totalDeposits = 0;
        double totalWithdrawals = 0;

        for (TransactionDTO transaction : transactions) {
            String type = transaction.getTransactionType();
            double amount = transaction.getAmount();

            if ("Deposit".equals(type) || "Interest".equals(type)) {
                totalDeposits += amount;
            } else if ("Withdrawal".equals(type)) {
                totalWithdrawals += amount;
            }
        }

        totalDepositsLabel.setText(String.format("BWP %,.2f", totalDeposits));
        totalWithdrawalsLabel.setText(String.format("BWP %,.2f", totalWithdrawals));
        netChangeLabel.setText(String.format("BWP %,.2f", totalDeposits - totalWithdrawals));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}