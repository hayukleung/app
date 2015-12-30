package com.hayukleung.designpattern.Visitor;

public class Man implements Person {

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}