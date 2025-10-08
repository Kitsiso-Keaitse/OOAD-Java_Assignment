package banking.model;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer owner;
    
    public Account(String accountNumber, Customer owner, String branch) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.branch = branch;
        this.balance = 0.0;
        
        // Automatically add this account to customer's accounts
        if (owner != null) {
            owner.openAccount(this);
        }
    }
    
    // Getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getOwner() { return owner; }
    
    // Business logic methods
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: BWP " + amount + " into account " + accountNumber);
        }
    }
    
    // Overloaded method with description
    public void deposit(double amount, String description) {
        deposit(amount);
        System.out.println("Description: " + description);
    }
    
    public String getAccountInfo() {
        return "Account: " + accountNumber + ", Balance: BWP " + balance + 
               ", Branch: " + branch + ", Type: " + this.getClass().getSimpleName();
    }
}