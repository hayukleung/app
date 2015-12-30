package com.hayukleung.designpattern.Mediator;

public class CDDriver extends Colleague {
    private String data = "";

    public CDDriver(Mediator mediator) {
        super(mediator);
    }

    public String getData() {
        return data;
    }

    public void readCD() {
        this.data = "";
        getMediator().changed(this);
    }
}