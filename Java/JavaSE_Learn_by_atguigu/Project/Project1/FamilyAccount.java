
public class FamilyAccount {
    public static void main(String[] args){
        boolean flag = true;
        int balance = 100000;
        // 记录用户的收入支出
        String details = "收支\t账户金额\t收支金额\t说   明\n";
        while(flag){
            System.out.println("-----------------家庭收支记账软件-----------------\n");
            System.out.println("                1.收支明细");
            System.out.println("                2.登记收入");
            System.out.println("                3.登记支出");
            System.out.println("                4.退出\n");
            System.out.println("                请选择(1-4)：____");

            // 获取用户的输入
            char selection = Utility.readMenuSelection();
            switch (selection){
                case '1':
                    System.out.println("-----------------当前收支明细记录-----------------");
                    System.out.println(details);
                    System.out.println("------------------------------------------------");
                    break;
                case '2':
                    System.out.println("本次收入金额:");
                    int addMoney = Utility.readNumber();
                    System.out.println("本次收入说明:");
                    String addInfo = Utility.readString();

                    // 更新balance
                    balance += addMoney;
                    // 更新details
                    details += ("收入\t" + balance + "\t\t" + addMoney + "\t\t" + addInfo + "\n");

                    System.out.println("-----------------登记完成-----------------\n");
                    break;
                case '3':
                    System.out.println("本次支出金额:");
                    int minusMoney = Utility.readNumber();
                    System.out.println("本次支出说明:");
                    String minusInfo = Utility.readString();

                    // 处理balance
                    if(balance >= minusMoney){
                        balance -= minusMoney;
                        details += ("支出\t" + balance + "\t\t" + minusMoney + "\t\t" + minusInfo + "\n");
                    }else{
                        System.out.println();
                    }
                    System.out.println("-----------------登记完成-----------------\n");
                    break;
                case '4':
                    System.out.println("确认是否退出(Y|N)");
                    char isExit = Utility.readConfirmSelection();
                    if(isExit == 'Y'){
                        flag = false;
                    }
                    break;
            }
        }

    }
}
