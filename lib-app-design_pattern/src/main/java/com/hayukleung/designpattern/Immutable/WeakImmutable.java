package com.hayukleung.designpattern.Immutable;

public class WeakImmutable {

    private int state;

    private User user;

    private Integer age;

    public WeakImmutable(int state, User user, Integer age) {
        this.state = state;
        this.user = user;
        this.age = age;
    }

    public int getState() {
        return this.state;
    }

    public User getUser() {
        return this.user;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setState() {
        // do nothing.
    }
}
