package main.java.banking.model;

public class CompanyCustomer extends Customer {
    private String companyName;
    private String registrationNumber;
    
    public CompanyCustomer(String address, String companyName, String registrationNumber) {
        super(address);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
    }
    
    // Getters and setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    @Override
    public String getCustomerInfo() {
        return "Company Customer: " + companyName + ", Reg No: " + registrationNumber + 
               ", Address: " + address;
    }
}