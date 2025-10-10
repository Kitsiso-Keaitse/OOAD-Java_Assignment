package main.java.banking.model;

import java.util.Date;

public class IndividualCustomer extends Customer {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private boolean employed;
    private String employer;
    
    public IndividualCustomer(String address, String firstName, String lastName, Date dateOfBirth) {
        super(address);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.employed = false;
        this.employer = "";
    }
    
    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public boolean isEmployed() { return employed; }
    public void setEmployed(boolean employed) { this.employed = employed; }
    
    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { 
        this.employer = employer;
        this.employed = (employer != null && !employer.trim().isEmpty());
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String getCustomerInfo() {
        return "Individual Customer: " + getFullName() + ", Address: " + address + 
               ", Employed: " + (employed ? "Yes at " + employer : "No");
    }
}