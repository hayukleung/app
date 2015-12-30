package com.hayukleung.designpattern.Iterator;

public class Test {
    public static void main(String[] args) {
        Aggregate ag = new ConcreteAggregate();
        ag.add("");
        ag.add("");
        ag.add("");
        Iterator it = ag.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            System.out.println(str);
        }
    }
}
