package com.hayukleung.designpattern.SimpleFactory;

public class Test {
    public static void main(String[] args) throws Exception {
        FruitGardener fg = new FruitGardener();
        Fruit ap = fg.factory("Apple");
        ap.grow();
        Fruit gp = fg.factory("Grape");
        gp.plant();

        Fruit dd = fg.factory("ddd");
    }
}
