package banking.model;

public class ChequeAccount extends Account implements Withdrawable {
    
    public ChequeAccount(String accountNumber, Customer owner, String branch) {
        super(accountNumber, owner, branch);
        
        // Validate that individual customers are employed
        if (owner instanceof IndividualCustomer) {
            IndividualCustomer indCustomer = (IndividualCustomer) owner;
            if (!indCustomer.isEmployed()) {
                throw new IllegalArgumentException("Individual customers must be employed to open a Cheque account");
            }
        }
    }
    
    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            balance -= amount;
            System.out.println("Withdrawn: BWP " + amount + " from Cheque account " + accountNumber);
        } else {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
    }
}