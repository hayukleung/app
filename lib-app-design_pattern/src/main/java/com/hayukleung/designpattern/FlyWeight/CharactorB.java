package com.hayukleung.designpattern.FlyWeight;

public class CharactorB extends Charactor {
    public CharactorB() {
        this.letter = 'B';
    }

    public void display() {
        try {
            System.out.println(this.letter);
        } catch (Exception err) {
        }
    }
}
