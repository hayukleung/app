package com.hayukleung.designpattern.Decorator;

public class ConcreteComponent implements Component {
    @Override
    public void doSomething() {
        System.out.println("A");
    }
}
