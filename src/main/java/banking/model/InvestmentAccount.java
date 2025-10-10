package main.java.banking.model;

public class InvestmentAccount extends Account implements InterestBearing, Withdrawable {
    public static final double MONTHLY_RATE = 0.05; // 5% as per requirements
    private static final double MIN_OPENING_BALANCE = 500.0;
    
    public InvestmentAccount(String accountNumber, Customer owner, String branch, double initialDeposit) {
        super(accountNumber, owner, branch);
        
        // Enforce minimum opening balance
        if (initialDeposit >= MIN_OPENING_BALANCE) {
            this.balance = initialDeposit;
        } else {
            throw new IllegalArgumentException("Investment account requires minimum opening balance of BWP " + MIN_OPENING_BALANCE);
        }
    }
    
    @Override
    public double calculateMonthlyInterest() {
        return balance * MONTHLY_RATE;
    }
    
    @Override
    public void applyMonthlyInterest() {
        double interest = calculateMonthlyInterest();
        balance += interest;
        System.out.println("Applied monthly interest: BWP " + interest + " to account " + accountNumber);
    }
    
    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: BWP " + amount + " from Investment account " + accountNumber);
        } else {
            throw new IllegalArgumentException("Invalid withdrawal amount or insufficient funds");
        }
    }
}