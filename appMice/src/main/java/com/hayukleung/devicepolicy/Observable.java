package com.hayukleung.devicepolicy;

/**
 * Observable.java
 * <p/>
 * Created by hayukleung on 3/22/16.
 */
public interface Observable {

  void set(Observer observer);

  void clear();

  void notice();
}
