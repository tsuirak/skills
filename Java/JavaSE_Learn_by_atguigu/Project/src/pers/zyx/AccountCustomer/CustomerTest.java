package pers.zyx.AccountCustomer;

public class CustomerTest {
    public static void main(String[] args) {
        Customer JaneSmith = new Customer("Jane","Smith");

        Account acc = new Account(1000, 2000,0.0123);

        JaneSmith.setAccount(acc);

        JaneSmith.getAccount().deposit(100);
        JaneSmith.getAccount().withdraw(960);
        JaneSmith.getAccount().withdraw(2000);

        System.out.println("Customer + " + "[" + JaneSmith.getLastName() + "," + JaneSmith.getFirstName() + "] " +
        "has a account : id is " + JaneSmith.getAccount().getId() + " ,annualInterestRate is " + JaneSmith.getAccount().getAnnualInterestRate() +
                " balance is " + JaneSmith.getAccount().getBalance());
    }
}
