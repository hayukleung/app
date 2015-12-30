package com.hayukleung.designpattern.SimpleFactory;

public class Apple implements Fruit {
    private int treeAge;

    public static void log(String msg) {
        System.out.println(msg);
    }

    public void grow() {
        log(" Apple is growing ");
    }

    public void harvest() {
        log(" Apple has been harvested ");
    }

    public void plant() {
        log(" Apple ha been planted ");
    }

    public int getTreeAge() {
        return treeAge;
    }

    public void setTreeAge(int treeAge) {
        this.treeAge = treeAge;
    }
}
