package com.hayukleung.designpattern.ChainOfResponsibility;

public class Teacher extends Handler {
    public Teacher(String name) {
        this.name = name;
    }

    public void handleRequest(String request) {
        if ("请假不去上课".equals(request)) {
            System.out.println(name + "可以处理" + request + ",给予批准！");
        } else {
            System.out.println(name + "不可以处理" + request + "转交给" + successor.getName());
            successor.handleRequest(request);
        }
    }

}
