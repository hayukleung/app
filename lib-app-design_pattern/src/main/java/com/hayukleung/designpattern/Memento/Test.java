package com.hayukleung.designpattern.Memento;

public class Test {
    public static void main(String[] args) {

        WindowsSystem Winxp = new WindowsSystem();
        User user = new User();
        Winxp.setState("");
        user.saveMemento(Winxp.createMemento());
        Winxp.setState("");
        Winxp.restoreMemento(user.retrieveMemento());
        System.out.println("" + Winxp.getState());
    }
}
