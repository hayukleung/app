package com.hayukleung.designpattern.Strategy;

public class PrimaryMemberStrategy implements MemberStrategy {

    @Override
    public double calcPrice(double booksPrice) {

        System.out.println("");
        return booksPrice;
    }

}
