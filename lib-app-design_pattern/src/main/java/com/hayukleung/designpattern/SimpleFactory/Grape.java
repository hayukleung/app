package com.hayukleung.designpattern.SimpleFactory;

public class Grape implements Fruit {
    private boolean seedless;

    public static void log(String msg) {
        System.out.println(msg);
    }

    public void grow() {
        log("Grape is growing.");
    }

    public void harvest() {
        log("Grape has been harvested");
    }

    public void plant() {
        log("Grape ha been planted");
    }

    public boolean isSeedless() {
        return seedless;
    }

    public void setSeedless(boolean seedless) {
        this.seedless = seedless;
    }

}
