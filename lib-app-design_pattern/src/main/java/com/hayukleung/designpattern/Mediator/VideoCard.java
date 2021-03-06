package com.hayukleung.designpattern.Mediator;

/**
 * @author lyq
 */
public class VideoCard extends Colleague {

    public VideoCard(Mediator mediator) {
        super(mediator);
    }

    public void showData(String data) {
        System.out.println("" + data);
    }
}