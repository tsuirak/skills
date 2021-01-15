package pers.zyx.AccountCustomerBank;

public class Bank {
    private Customer[] customer;
    private int numberOfCustomers;

    public Bank(){
        customer = new Customer[100];
    }

    // 添加客户
    public void addCustomer(String f , String l){
        Customer cust = new Customer(f , l);
        customer[numberOfCustomers++] = cust;
    }

    // 获取客户个数
    public int getNumOfCustomers(){
        return numberOfCustomers;
    }

    // 获取指定位置上的客户
    public Customer getCustomer(int index){
        if(index >= 0 && index < numberOfCustomers){
            return customer[index];
        }
        else{
            return null;
        }
    }
}
