package com.hayukleung.designpattern.Strategy;

public class IntermediateMemberStrategy implements MemberStrategy {

    @Override
    public double calcPrice(double booksPrice) {

        System.out.println("");
        return booksPrice * 0.9;
    }

}
