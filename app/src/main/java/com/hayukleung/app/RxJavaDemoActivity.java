package com.hayukleung.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;

import com.hayukleung.app.util.ToastUtil;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * RxJavaDemoActivity.java
 * <p>
 * Created by hayukleung on 12/30/15.
 */
public class RxJavaDemoActivity extends Activity {

    private static final String TAG = "RxAndroidSamples";

    private Handler backgroundHandler;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_rx_java);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        View view = findViewById(R.id.button_run_scheduler);
        RxView.clicks(view) // 以 Observable 形式来反馈点击事件
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        test();
                    }
                });
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }

    private void test() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("test");
                subscriber.onCompleted();
            }
        });
//        Subscriber<String> subscriber = new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//                ToastUtil.showToast(RxJavaDemoActivity.this, s);
//            }
//        };
//        observable.subscribe(subscriber);

        // 假如并不关心OnComplete和OnError，我们只需要在onNext的时候做一些处理，这时候就可以使用Action1类
//        Action1<String> actionNext = s -> ToastUtil.showToast(RxJavaDemoActivity.this, s);
//        observable.subscribe(actionNext);

        // Observable.just就是用来创建只发出一个事件就结束的Observable对象
//        Observable.just("Hello RxJava")
//                .subscribe(s -> ToastUtil.showToast(RxJavaDemoActivity.this, s));

        // 操作符就是为了解决对Observable对象的变换的问题，操作符用于在Observable和最终的Subscriber之间修改Observable发出的事件
//        Observable.just("hello")
//                .map(s -> String.format("**%s**", s))
//                .subscribe(s -> ToastUtil.showToast(RxJavaDemoActivity.this, s));

        // Observable.from()方法，它接收一个集合作为输入，然后每次输出一个元素给subscriber
        query("y")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        Observable
                                .from(strings)
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        ToastUtil.showToast(RxJavaDemoActivity.this, s);
                                    }
                                });
                    }
                });
    }

    /**
     * 搜索引擎
     *
     * @param keyword
     *              输入y，查询得到result1，result2
     *              输入其它，查询得到result3，result4
     * @return
     */
    Observable<List<String>> query(final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                if ("y".equals(keyword)) {
                    List<String> results = new ArrayList<>(2);
                    results.add("result1");
                    results.add("result2");
                    subscriber.onNext(results);
                } else {
                    List<String> results = new ArrayList<>(2);
                    results.add("result3");
                    results.add("result4");
                    subscriber.onNext(results);
                }
                subscriber.onCompleted();
            }
        });
    }
}
