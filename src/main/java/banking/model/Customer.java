package banking.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected String address;
    protected List<Account> accounts;
    
    public Customer(String address) {
        this.address = address;
        this.accounts = new ArrayList<>();
    }
    
    // Getters and setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public List<Account> getAccounts() { return accounts; }
    
    // Business logic methods
    public void openAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }
    
    public abstract String getCustomerInfo();
}