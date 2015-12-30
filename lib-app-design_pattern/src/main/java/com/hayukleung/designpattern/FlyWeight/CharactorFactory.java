package com.hayukleung.designpattern.FlyWeight;

import java.util.Hashtable;

public class CharactorFactory {
    private Hashtable<String, Charactor> charactors = new Hashtable<String, Charactor>();

    public CharactorFactory() {
        charactors.put("A", new CharactorA());
        charactors.put("B", new CharactorB());
    }

    public Charactor getCharactor(String key) {
        Charactor charactor = (Charactor) charactors.get(key);
        if (charactor == null) {
            if (key.equals("A")) {
                charactor = new CharactorA();
            } else if (key.equals("B")) {
                charactor = new CharactorB();
            }
            charactors.put(key, charactor);
        }
        return charactor;
    }
}