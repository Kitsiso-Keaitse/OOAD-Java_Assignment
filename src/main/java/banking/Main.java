package banking;

import banking.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        
        // Initialize database with tables and sample data
        DatabaseConnection.initializeDatabase();
        
        // Set up main stage properties
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        showLoginView();
    }
    
    public static void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("loginUI.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("First Financial Bank - Login");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Failed to load login view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Application Error", "Failed to load login interface. Please restart the application.");
        }
    }
    
    public static void showCustomerDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("customerDashboardUI.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("First Financial Bank - Customer Dashboard");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Failed to load customer dashboard: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Navigation Error", "Failed to load customer dashboard. Returning to login.");
            showLoginView();
        }
    }
    
    public static void showBankClerkDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("bankClerkDashboardUI.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("First Financial Bank - Bank Clerk Dashboard");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Failed to load bank clerk dashboard: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Navigation Error", "Failed to load clerk dashboard. Returning to login.");
            showLoginView();
        }
    }
    
    public static void showAccountOpeningView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("accountOpeningUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Open New Account - First Financial Bank");
            stage.setScene(new Scene(root, 700, 550));
            stage.setResizable(false);
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load account opening view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Window Error", "Failed to open account creation window.");
        }
    }
    
    public static void showTransactionHistoryView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("transactionHistoryUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Transaction History - First Financial Bank");
            stage.setScene(new Scene(root, 1100, 750));
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.initOwner(primaryStage);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load transaction history view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Window Error", "Failed to open transaction history window.");
        }
    }
    
    public static void showNewCustomerRegistrationView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("newCustomerUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Register New Customer - First Financial Bank");
            stage.setScene(new Scene(root, 600, 500));
            stage.setResizable(false);
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load new customer registration view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Window Error", "Failed to open customer registration window.");
        }
    }
    
    public static void showDepositView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("depositUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Make a Deposit - First Financial Bank");
            stage.setScene(new Scene(root, 500, 400));
            stage.setResizable(false);
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load deposit view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Window Error", "Failed to open deposit window.");
        }
    }
    
    public static void showTransferView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("transferUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Transfer Funds - First Financial Bank");
            stage.setScene(new Scene(root, 500, 400));
            stage.setResizable(false);
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load transfer view: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Window Error", "Failed to open transfer window.");
        }
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void closeApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        try {
            // Check if database driver is available
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC driver loaded successfully.");
            
            // Launch the JavaFX application
            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Please ensure the driver is in the classpath.");
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                showErrorDialog("Database Error", 
                    "SQLite JDBC driver not found. Please ensure the application is properly configured.");
            });
        } catch (Exception e) {
            System.err.println("Failed to launch application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}