package com.hayukleung.designpattern.Strategy;

public class Test {
    public static void main(String[] args) {
        MemberStrategy strategy = new AdvancedMemberStrategy();

        Price price = new Price(strategy);
        double quote = price.quote(300);
        System.out.println("" + quote);

        AdvancedMemberStrategy strategy2 = new AdvancedMemberStrategy();
        price = new Price(strategy2);
        quote = price.quote(300);
        System.out.println("" + quote);
    }
}
