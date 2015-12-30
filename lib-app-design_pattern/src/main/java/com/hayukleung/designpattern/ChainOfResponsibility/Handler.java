package com.hayukleung.designpattern.ChainOfResponsibility;

public abstract class Handler {

    protected Handler successor;
    protected String name;

    /**
     * 处理请求，由子类完成
     *
     * @param request
     */
    public abstract void handleRequest(String request);

    /**
     * 设置下一个处理请求的人
     *
     * @param successor
     */
    public void setNextHandler(Handler successor) {
        this.successor = successor;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }
}
