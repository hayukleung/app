package com.hayukleung.designpattern.TemplateMethod;

public class Test {
    public static void main(String[] args) {
        Account account = new MoneyMarketAccount();
        System.out.println("" + account.calculateInterest());
        account = new CDAccount();
        System.out.println("" + account.calculateInterest());
    }
}
