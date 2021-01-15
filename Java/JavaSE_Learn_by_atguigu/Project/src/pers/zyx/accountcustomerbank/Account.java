package pers.zyx.accountcustomerbank;

public class Account {
    private double balance;

    public Account(double initBalance){
        this.balance = initBalance;
    }
    public double getBalance(){
        return balance;
    }

    // 存钱
    public void deposit(double amt){
        if(amt > 0){
            balance += amt;
            System.out.println("存钱成功");
        }
    }

    // 取钱
    public void withdraw(double amt){
        if(balance >= amt){
            balance -= amt;
            System.out.println("取钱成功");
        }else{
            System.out.println("余额不足");
        }
    }

}
