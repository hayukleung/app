package com.hayukleung.designpattern.Bridge;

public class Test {
    public static void main(String[] args) {
        Shape circle = new Circle(new Drawing2());
        circle.doDraw();
    }
}
