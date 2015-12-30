package com.hayukleung.designpattern.Immutable;

public class Test {
    public static void main(String[] args) {
        int state = 0;
        User u = new User();
        Integer age = 100;
        u.setName("yes");
        WeakImmutable weak = new WeakImmutable(state, u, age);
        System.out.println("" + weak.getState() + ","
                + weak.getUser().getName() + "," + weak.getAge());
        state = 5;
        u.setName("no");
        age = 200;
        System.out.println("" + weak.getState() + ","
                + weak.getUser().getName() + "," + weak.getAge());
    }
}
