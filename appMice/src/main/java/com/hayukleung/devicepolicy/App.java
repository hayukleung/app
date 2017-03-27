package com.hayukleung.devicepolicy;

import android.support.multidex.MultiDexApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * App.java
 * <p/>
 * Created by hayukleung on 3/22/16.
 */
public class App extends MultiDexApplication implements Observable {

  private List<Observer> mObservableList = new ArrayList<>(1);

  @Override public void set(Observer observer) {
    if (null == mObservableList) {
      mObservableList = new ArrayList<>(1);
    }
    mObservableList.clear();
    mObservableList.add(observer);
  }

  @Override public void clear() {
    if (null == mObservableList) {
      return;
    }
    mObservableList.clear();
  }

  @Override public void notice() {
    if (null == mObservableList) {
      return;
    }
    for (Observer observer : mObservableList) {
      if (null != observer) {
        observer.update();
      }
    }
  }
}
