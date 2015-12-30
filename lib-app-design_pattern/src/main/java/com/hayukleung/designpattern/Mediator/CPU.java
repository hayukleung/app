package com.hayukleung.designpattern.Mediator;

public class CPU extends Colleague {
    private String videoData = "";
    private String soundData = "";

    public CPU(Mediator mediator) {
        super(mediator);
    }

    public String getVideoData() {
        return videoData;
    }

    public String getSoundData() {
        return soundData;
    }

    public void executeData(String data) {
        String[] array = data.split(",");
        this.videoData = array[0];
        this.soundData = array[1];
        getMediator().changed(this);
    }

}