package com.hayukleung.designpattern.ChainOfResponsibility;

public class Monitor extends Handler {
    public Monitor(String name) {
        this.name = name;
    }

    public void handleRequest(String request) {
        if ("不去开班会".equals(request)) {
            System.out.println(name + "可以处理" + request + ",给予批准！");
        } else {
            System.out.println(name + "不可以处理" + request + "转交给" + successor.getName());
            successor.handleRequest(request);
        }
    }
}
