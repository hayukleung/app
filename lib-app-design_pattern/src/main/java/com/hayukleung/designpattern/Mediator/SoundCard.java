package com.hayukleung.designpattern.Mediator;

/**
 * @author lyq
 */
public class SoundCard extends Colleague {

    public SoundCard(Mediator mediator) {
        super(mediator);
    }

    public void soundData(String data) {
        System.out.println("" + data);
    }
}