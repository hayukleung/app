package com.hayukleung.designpattern.FlyWeight;

/**
 * @author lyq
 */
public class Test {
    public static void main(String[] args) {
        Charactor a = new CharactorA();
        Charactor b = new CharactorB();

        display(a, 12);
        display(b, 14);
    }

    public static void display(Charactor objChar, int nSize) {
        try {
            System.out.println("" + objChar.letter + "" + nSize);
        } catch (Exception err) {
        }
    }
}
