package com.hayukleung.designpattern.Builder;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Builder shandongCuisineChef = new ShandongCuisineChefBuilder();
        Builder siChuanChef = new SiChuanChefBuilder();

        Director director = new Director(shandongCuisineChef);
        Food food = director.construct("", "", "", "", "", "");

        System.out.println("" + "\r\n" + food);

        Director director2 = new Director();
        director2.setDirector(siChuanChef);
        Food food2 = director2.construct("", "", "", "", "", "");
        System.out.println(" " + "\r\n" + food2);

    }
}
