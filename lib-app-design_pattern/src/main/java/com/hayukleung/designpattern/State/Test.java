package com.hayukleung.designpattern.State;

public class Test {
    public static void main(String[] args) {
        Person p = new Person();
        for (int i = 0; i < 10; i++)
            p.doSomething();
    }
}
