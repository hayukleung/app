package com.hayukleung.designpattern.Decorator;

public class Test {
    public static void main(String[] args) {
        Component component = new ConcreteDecorator2(new ConcreteDecorator1(
                new ConcreteComponent()));

        component.doSomething();

    }

}
