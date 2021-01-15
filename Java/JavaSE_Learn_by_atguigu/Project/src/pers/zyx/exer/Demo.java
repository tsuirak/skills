package pers.zyx.exer;

public class Demo{
    public static void main(String[] args) {

    }
}

class Person{
    private String name;
    private int age;

    private Person(){

    }

    private void setName(String name){
        this.name = name;
    }

    private String getName(){
        return name;
    }

    private void setAge(int age){
        this.age = age;
    }

    private int getAge(){
        return age;
    }
}

