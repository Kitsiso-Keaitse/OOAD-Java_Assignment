package bankingsystem.bankingsystem.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import bankingsystem.bankingsystem.*;
import bankingsystem.bankingsystem.database.AccountDAO;
import bankingsystem.bankingsystem.database.CustomerDAO;

import java.util.HashMap;
import java.util.Map;

public class AccountOpeningController {

    @FXML private ComboBox<String> customerComboBox;
    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField branchField;
    @FXML private TextField initialDepositField;
    @FXML private TextField employerField;
    @FXML private TextField companyAddressField;
    @FXML private VBox employmentInfoBox;

    private CustomerDAO customerDAO = new CustomerDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private Map<String, Customer> customerMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupAccountTypeComboBox();
        loadCustomers();
        
        accountTypeComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleAccountTypeChange(newValue)
        );
    }

    private void setupAccountTypeComboBox() {
        accountTypeComboBox.setItems(FXCollections.observableArrayList(
            "Savings", "Investment", "Cheque"
        ));
    }

    private void loadCustomers() {
        try {
            var customers = customerDAO.getAllCustomers();
            customerComboBox.getItems().clear();
            customerMap.clear();
            
            for (Customer customer : customers) {
                String displayName;
                if (customer instanceof IndividualCustomer) {
                    IndividualCustomer ind = (IndividualCustomer) customer;
                    displayName = ind.getFullName() + " (Individual)";
                } else {
                    CompanyCustomer comp = (CompanyCustomer) customer;
                    displayName = comp.getCompanyName() + " (Company)";
                }
                customerComboBox.getItems().add(displayName);
                customerMap.put(displayName, customer);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load customers: " + e.getMessage());
        }
    }

    @FXML
    private void handleAccountTypeChange(String accountType) {
        if (accountType != null) {
            employmentInfoBox.setVisible("Cheque".equals(accountType));
            
            // Clears employment fields when not needed
            if (!"Cheque".equals(accountType)) {
                employerField.clear();
                companyAddressField.clear();
            }
        }
    }

    @FXML
    private void handleOpenAccount() {
        if (validateInput()) {
            createAccount();
        }
    }

    @FXML
    private void handleCancel() {
        branchField.getScene().getWindow().hide();
    }

    private boolean validateInput() {
        // Validate customer selection
        if (customerComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer");
            return false;
        }

        // Validate account type
        if (accountTypeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account type");
            return false;
        }

        // Validate branch
        if (branchField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter branch information");
            return false;
        }

        // Validate initial deposit
        String accountType = accountTypeComboBox.getValue();
        try {
            double initialDeposit = Double.parseDouble(initialDepositField.getText());
            
            if (initialDeposit <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Initial deposit must be positive");
                return false;
            }
            
            if ("Investment".equals(accountType) && initialDeposit < 500.0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Investment account requires minimum opening balance of BWP 500.00");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid initial deposit amount");
            return false;
        }

        // Validate employment info for cheque accounts
        if ("Cheque".equals(accountType)) {
            Customer customer = customerMap.get(customerComboBox.getValue());
            if (customer instanceof IndividualCustomer) {
                IndividualCustomer indCustomer = (IndividualCustomer) customer;
                if (!indCustomer.isEmployed()) {
                    if (employerField.getText().trim().isEmpty() || companyAddressField.getText().trim().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please provide employment information for cheque account");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void createAccount() {
        try {
            String customerDisplayName = customerComboBox.getValue();
            String accountType = accountTypeComboBox.getValue();
            String branch = branchField.getText().trim();
            double initialDeposit = Double.parseDouble(initialDepositField.getText());
            
            Customer customer = customerMap.get(customerDisplayName);
            String accountNumber = generateAccountNumber();
            
            Account account = createAccountInstance(accountType, accountNumber, customer, branch, initialDeposit);
            
            if (account != null) {
                // Make initial deposit
                account.deposit(initialDeposit, "Initial deposit");
                
                // Save to database
                if (accountDAO.saveAccount(account)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        String.format("Account opened successfully!\n\nAccount Number: %s\nAccount Type: %s\nBranch: %s\nInitial Deposit: BWP %,.2f",
                        accountNumber, accountType, branch, initialDeposit));
                    
                    // Clear form for next account
                    resetForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save account to database");
                }
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Account createAccountInstance(String accountType, String accountNumber, 
                                        Customer customer, String branch, double initialDeposit) {
        try {
            switch (accountType) {
                case "Savings":
                    return new SavingsAccount(accountNumber, customer, branch);
                    
                case "Investment":
                    return new InvestmentAccount(accountNumber, customer, branch, initialDeposit);
                    
                case "Cheque":
                    // Update employment info if provided
                    if (customer instanceof IndividualCustomer) {
                        IndividualCustomer indCustomer = (IndividualCustomer) customer;
                        if (!indCustomer.isEmployed() && !employerField.getText().trim().isEmpty()) {
                            indCustomer.setEmployer(employerField.getText().trim());
                            indCustomer.setEmployed(true);
                            //
                        }
                    }
                    return new ChequeAccount(accountNumber, customer, branch);
                    
                default:
                    throw new IllegalArgumentException("Unknown account type: " + accountType);
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            return null;
        }
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }

    private void resetForm() {
        customerComboBox.getSelectionModel().clearSelection();
        accountTypeComboBox.getSelectionModel().clearSelection();
        branchField.clear();
        initialDepositField.clear();
        employerField.clear();
        companyAddressField.clear();
        employmentInfoBox.setVisible(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}