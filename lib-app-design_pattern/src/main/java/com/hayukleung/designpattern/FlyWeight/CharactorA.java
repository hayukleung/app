package com.hayukleung.designpattern.FlyWeight;

public class CharactorA extends Charactor {
    public CharactorA() {
        this.letter = 'A';
    }

    public void display() {
        try {
            System.out.println(this.letter);
        } catch (Exception err) {
        }
    }
}
