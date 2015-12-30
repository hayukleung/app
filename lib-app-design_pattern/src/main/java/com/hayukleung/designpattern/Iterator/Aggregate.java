package com.hayukleung.designpattern.Iterator;

public interface Aggregate {
    public void add(Object obj);

    public void remove(Object obj);

    public Iterator iterator();
}
