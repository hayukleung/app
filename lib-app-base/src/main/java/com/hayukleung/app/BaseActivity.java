package com.hayukleung.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hayukleung.app.base.R;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(getName());
        if (savedInstanceState == null) {
            if (fragment == null) {
                fragment = newFragment();
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, getName()).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = getFragment();
        if (fragment == null || !fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    protected BaseFragment getFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(getName());
    }

    protected abstract String getName();

    protected abstract BaseFragment newFragment();

    @Override
    public void finish() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.finish();
    }
}
