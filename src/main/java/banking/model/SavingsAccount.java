package banking.model;

public class SavingsAccount extends Account implements InterestBearing {
    public static final double INDIVIDUAL_RATE = 0.025; // 2.5% as per requirements
    public static final double COMPANY_RATE = 0.075;    // 7.5% as per requirements
    
    public SavingsAccount(String accountNumber, Customer owner, String branch) {
        super(accountNumber, owner, branch);
    }
    
    
    @Override
    public double calculateMonthlyInterest() {
        double rate = (owner instanceof IndividualCustomer) ? INDIVIDUAL_RATE : COMPANY_RATE;
        return balance * rate;
    }
    
    @Override
    public void applyMonthlyInterest() {
        double interest = calculateMonthlyInterest();
        balance += interest;
        System.out.println("Applied monthly interest: BWP " + interest + " to account " + accountNumber);
    }
    

}