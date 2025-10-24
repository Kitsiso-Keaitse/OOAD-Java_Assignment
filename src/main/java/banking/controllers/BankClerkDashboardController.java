package bankingsystem.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import bankingsystem.model.Main;
import bankingsystem.database.CustomerDAO;
import bankingsystem.database.AccountDAO;
import bankingsystem.database.TransactionDAO;
import bankingsystem.models.UserSession;
import bankingsystem.models.CustomerDTO;

import java.util.List;
import java.util.Optional;

public class BankClerkDashboardController {

    @FXML private Label totalCustomersLabel;
    @FXML private Label accountsOpenedLabel;
    @FXML private Label pendingActionsLabel;
    @FXML private TableView<CustomerDTO> customersTable;
    @FXML private TableColumn<CustomerDTO, String> customerIdColumn;
    @FXML private TableColumn<CustomerDTO, String> customerNameColumn;
    @FXML private TableColumn<CustomerDTO, String> customerTypeColumn;
    @FXML private TableColumn<CustomerDTO, String> accountsColumn;
    @FXML private TableColumn<CustomerDTO, String> joinDateColumn;

    private CustomerDAO customerDAO = new CustomerDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserSession session = UserSession.getInstance();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardStats();
        loadCustomers();
    }

    private void setupTableColumns() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        customerTypeColumn.setCellValueFactory(new PropertyValueFactory<>("customerType"));
        accountsColumn.setCellValueFactory(new PropertyValueFactory<>("accountCount"));
        joinDateColumn.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
    }

    @FXML
    private void showDashboard() {
        loadDashboardStats();
        loadCustomers();
    }

    @FXML
    private void showAccountOpening() {
        Main.showAccountOpeningView();
    }

    @FXML
    private void showCustomerManagement() {
        loadCustomers();
        showAlert(Alert.AlertType.INFORMATION, "Customer Management", "Customer list refreshed.");
    }

    @FXML
    private void showTransactionHistory() {
        Main.showTransactionHistoryView();
    }

    @FXML
    private void handleLogout() {
        session.clearSession();
        Main.showLoginView();
    }

    @FXML
    private void handleNewCustomer() {
        showNewCustomerDialog();
    }

    @FXML
    private void handleOpenAccount() {
        Main.showAccountOpeningView();
    }

    @FXML
    private void handleViewAllTransactions() {
        Main.showTransactionHistoryView();
    }

    @FXML
    private void handleGenerateReports() {
        showReportDialog();
    }

    private void loadDashboardStats() {
        try {
            // Get total customers count
            List<CustomerDTO> allCustomers = customerDAO.getAllCustomerDTOs();
            totalCustomersLabel.setText(String.valueOf(allCustomers.size()));

            // Get total accounts count
            int totalAccounts = allCustomers.stream()
                .mapToInt(CustomerDTO::getAccountCount)
                .sum();
            accountsOpenedLabel.setText(String.valueOf(totalAccounts));

            // Pending actions
            pendingActionsLabel.setText("3");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard statistics: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            List<CustomerDTO> customers = customerDAO.getAllCustomerDTOs();
            customersTable.setItems(FXCollections.observableArrayList(customers));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void showNewCustomerDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New Customer");
        dialog.setHeaderText("Create a new customer profile");

        VBox content = new VBox(10);
        
        ComboBox<String> customerTypeCombo = new ComboBox<>();
        customerTypeCombo.setItems(FXCollections.observableArrayList("Individual", "Company"));
        customerTypeCombo.setPromptText("Customer Type");
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        
        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        companyNameField.setVisible(false);
        
        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Registration Number");
        regNumberField.setVisible(false);
        
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        TextField employerField = new TextField();
        employerField.setPromptText("Employer (optional)");

        // Show/hide fields based on customer type
        customerTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isIndividual = "Individual".equals(newVal);
            firstNameField.setVisible(isIndividual);
            lastNameField.setVisible(isIndividual);
            dobPicker.setVisible(isIndividual);
            employerField.setVisible(isIndividual);
            companyNameField.setVisible(!isIndividual);
            regNumberField.setVisible(!isIndividual);
        });

        content.getChildren().addAll(
            new Label("Customer Type:"), customerTypeCombo,
            new Label("First Name:"), firstNameField,
            new Label("Last Name:"), lastNameField,
            new Label("Company Name:"), companyNameField,
            new Label("Registration Number:"), regNumberField,
            new Label("Date of Birth:"), dobPicker,
            new Label("Address:"), addressField,
            new Label("Employer:"), employerField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            registerNewCustomer(customerTypeCombo.getValue(), firstNameField.getText(), 
                              lastNameField.getText(), companyNameField.getText(),
                              regNumberField.getText(), dobPicker.getValue(),
                              addressField.getText(), employerField.getText());
        }
    }

    private void showReportDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Generate Reports");
        dialog.setHeaderText("Select report type and period");

        VBox content = new VBox(10);
        
        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.setItems(FXCollections.observableArrayList(
            "Customer Summary", "Account Activity", "Transaction History", "Financial Summary"
        ));
        reportTypeCombo.setPromptText("Report Type");
        
        DatePicker fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("From Date");
        
        DatePicker toDatePicker = new DatePicker();
        toDatePicker.setPromptText("To Date");

        content.getChildren().addAll(
            new Label("Report Type:"), reportTypeCombo,
            new Label("From Date:"), fromDatePicker,
            new Label("To Date:"), toDatePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            generateReport(reportTypeCombo.getValue(), fromDatePicker.getValue(), toDatePicker.getValue());
        }
    }

    private void registerNewCustomer(String customerType, String firstName, String lastName, 
                                   String companyName, String regNumber, java.time.LocalDate dob,
                                   String address, String employer) {
        // Validate input
        if (customerType == null || address == null || address.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields");
            return;
        }

        if ("Individual".equals(customerType)) {
            if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "First name and last name are required for individual customers");
                return;
            }
        } else if ("Company".equals(customerType)) {
            if (companyName == null || companyName.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Company name is required for company customers");
                return;
            }
        }

        try {
            // Create the appropriate customer type
            bankingsystem.bankingsystem.Customer customer;
            if ("Individual".equals(customerType)) {
                customer = new bankingsystem.bankingsystem.IndividualCustomer(
                    address, firstName, lastName, java.sql.Date.valueOf(dob)
                );
                if (employer != null && !employer.trim().isEmpty()) {
                    ((bankingsystem.bankingsystem.IndividualCustomer) customer).setEmployer(employer.trim());
                    ((bankingsystem.bankingsystem.IndividualCustomer) customer).setEmployed(true);
                }
            } else {
                customer = new bankingsystem.bankingsystem.CompanyCustomer(
                    address, companyName, regNumber
                );
            }

            // Save to database
            if (customerDAO.saveCustomer(customer)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer registered successfully!");
                loadDashboardStats();
                loadCustomers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save customer to database");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateReport(String reportType, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        if (reportType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a report type");
            return;
        }

        //generate and display the report
        showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
            String.format("%s report for period %s to %s has been generated.", 
                         reportType, fromDate, toDate));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}