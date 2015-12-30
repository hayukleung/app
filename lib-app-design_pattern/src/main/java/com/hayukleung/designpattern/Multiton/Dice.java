package com.hayukleung.designpattern.Multiton;

import java.util.Random;

public class Dice {

    public static final int INSTANCE1 = 0;
    public static final int INSTANCE2 = 1;

    private static final Dice DICE_INSTANCE1 = new Dice();
    private static final Dice DICE_INSTANCE2 = new Dice();

    private Dice() {

    }

    public static Dice getInstance(int which) {

        switch (which) {
            case INSTANCE1:
                return DICE_INSTANCE1;
            case INSTANCE2:
                return DICE_INSTANCE2;
            default:
                try {
                    throw new Exception("" + which + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public synchronized int roll() {

        try {
            Random ran = new Random(System.currentTimeMillis());
            int rollValue = ran.nextInt();
            rollValue %= 6;
            Thread.sleep(6);
            return rollValue > 0 ? ++rollValue : --rollValue * -1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
