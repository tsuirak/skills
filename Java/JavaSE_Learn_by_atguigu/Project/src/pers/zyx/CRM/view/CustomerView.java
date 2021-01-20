package pers.zyx.CRM.view;

import pers.zyx.CRM.bean.Customer;
import pers.zyx.CRM.service.CustomerList;
import pers.zyx.CRM.util.CMUtility;


public class CustomerView {

    private CustomerList customerList = new CustomerList(10);

    public CustomerView(){
        Customer cust = new Customer("马云",'男',25,"66776677","mayun@gmail.com");
        customerList.addCustomer(cust);
    }

    public void enterMainMenu(){

        boolean isFlag = true;
        while(isFlag){
            System.out.println("\n-----------------客户信息管理软件-----------------\n");
            System.out.println("                   1 添 加 客 户");
            System.out.println("                   2 修 改 客 户");
            System.out.println("                   3 删 除 客 户");
            System.out.println("                   4 客 户 列 表");
            System.out.println("                   5 退       出\n");
            System.out.print("                   请选择(1-5)：");

            char menu = CMUtility.readMenuSelection();
            switch (menu){
                case '1':
                    addNewCustomer();
                    break;
                case '2':
                    modifyCustomer();
                    break;
                case '3':
                    deleteCustomer();
                    break;
                case '4':
                    listAllCustomers();
                    break;
                case '5':
                    System.out.println("确认是否退出(Y|N)");
                    char isExit = CMUtility.readConfirmSelection();
                    if(isExit == 'Y'){
                        isFlag = false;
                    }
            }

        }

    }
    private void addNewCustomer(){
        System.out.println("---------------------添加客户---------------------");
        System.out.print("姓名：");
        String name = CMUtility.readString(4);
        System.out.print("性别：");
        char gender = CMUtility.readChar();
        System.out.print("年龄：");
        int age = CMUtility.readInt();
        System.out.print("电话：");
        String phone = CMUtility.readString(15);
        System.out.print("邮箱：");
        String email = CMUtility.readString(15);

        Customer customer = new Customer(name,gender,age,phone,email);
        boolean flag = customerList.addCustomer(customer);
        if(flag == true){
            System.out.println("---------------------添加完成---------------------");
        }else{
            System.out.println("---------------------添加失败---------------------");
        }
    }

    private void modifyCustomer(){
        System.out.println("---------------------修改客户---------------------");
        Customer cust;
        int index;
        for( ; ; ){
            System.out.print("请选择待修改客户编号(-1退出)：");
            index = CMUtility.readInt();
            if(index == -1) {
                return;
            }

            cust = customerList.getCustomer(index - 1);
            if(cust == null){
                System.out.println("无法找到指定客户！");
            }else {
                break;
            }
        }
        System.out.print("姓名(" + cust.getName() + ")：");
        String name = CMUtility.readString(4, cust.getName());

        System.out.print("性别(" + cust.getGender() + ")：");
        char gender = CMUtility.readChar(cust.getGender());

        System.out.print("年龄(" + cust.getAge() + ")：");
        int age = CMUtility.readInt(cust.getAge());

        System.out.print("电话(" + cust.getPhone() + ")：");
        String phone = CMUtility.readString(15, cust.getPhone());

        System.out.print("邮箱(" + cust.getEmail() + ")：");
        String email = CMUtility.readString(15, cust.getEmail());

        cust = new Customer(name, gender, age, phone, email);
        boolean isReplace = customerList.replaceCustomer(index - 1, cust);

        if (isReplace) {
            System.out
                    .println("---------------------修改完成---------------------");
        } else {
            System.out.println("----------无法找到指定客户,修改失败--------------");
        }
    }

    private void deleteCustomer(){
        System.out.println("---------------------删除客户---------------------");
        int index;
        Customer cust;
        for (;;) {
            System.out.print("请选择待删除客户编号(-1退出)：");
            index = CMUtility.readInt();
            if (index == -1) {
                return;
            }

            cust = customerList.getCustomer(index - 1);
            if (cust == null) {
                System.out.println("无法找到指定客户！");
            } else
                break;
        }

        System.out.print("确认是否删除(Y|N)：");
        char yn = CMUtility.readConfirmSelection();
        if (yn == 'N')
            return;

        boolean isDelete = customerList.deleteCustomer(index - 1);
        if (isDelete) {
            System.out.println("---------------------删除完成---------------------");
        } else {
            System.out.println("----------无法找到指定客户,删除失败--------------");
        }

    }
    private void listAllCustomers(){
        System.out.println("---------------------添加客户---------------------");
        int total = customerList.getTotal();
        if(total == 0){
            System.out.println("没有客户记录");
        }else {
            System.out.println("编号\t姓名\t性别\t年龄\t\t电话\t\t邮箱");
            Customer[] custs = customerList.getAllCustomers();
            for(int i = 0 ; i < custs.length ; i ++ ){
                Customer cust = custs[i];
                System.out.println((i + 1) + "\t" + cust.getName() + "\t" + cust.getAge()+ "\t" + cust.getGender() + "\t" + cust.getPhone() + "\t" +
                        cust.getEmail());
            }
        }
        System.out.println("------------------客户列表加载完毕------------------");


    }
    public static void main(String[] args){
        CustomerView cView = new CustomerView();
        cView.enterMainMenu();
    }
}
