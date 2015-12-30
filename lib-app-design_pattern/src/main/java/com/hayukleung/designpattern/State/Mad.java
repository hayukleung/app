package com.hayukleung.designpattern.State;

public class Mad implements MoodState {
    Person p;

    public Mad(Person p) {
        this.p = p;
    }

    public void doSomething() {
        System.out.println("I'm Mad");
    }

    public void changeState() {
        p.setState(new Angry(p));
    }
}