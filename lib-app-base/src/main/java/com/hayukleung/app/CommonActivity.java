package com.hayukleung.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用 Activity
 */
public class CommonActivity extends BaseActivity {
    static final String FRAGMENT_NAME = "fragment_name";
    private static final String FRAGMENT_RECORD = "fragment_record";
    private String mFragmentName;
    private Toast mShowToast = null;
    private ArrayList<Integer> mFragmentRecord = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mFragmentName = intent.getStringExtra(FRAGMENT_NAME);
        if (TextUtils.isEmpty(mFragmentName)) {
//            throw new IllegalStateException("Must have a fragment name for comment activity.");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getName() {
        return mFragmentName;
    }

    @Override
    protected BaseFragment newFragment() {
        return (BaseFragment) Fragment.instantiate(this, mFragmentName, getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(FRAGMENT_RECORD, mFragmentRecord);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mFragmentRecord = savedInstanceState.getIntegerArrayList(FRAGMENT_RECORD);
    }

    public void setFragmentRecord(Fragment fragment) {
        Fragment node = fragment;
        while (node != null) {
            int index = FragmentHelper.getIndex(node);
            if (index < 0) {
                throw new IllegalStateException("Fragment is out of FragmentManager: " + node);
            }
            mFragmentRecord.add(0, index);
            node = node.getParentFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentHelper.noteStateNotSaved(this);
        List<Fragment> active = FragmentHelper.getActive(this);
        Fragment fragment = null;
        for (Integer index : mFragmentRecord) {
            if (active != null && index >= 0 && index < active.size()) {
                fragment = active.get(index);
                if (fragment == null) {
                    Log.w(CommonActivity.class.getSimpleName(), "Activity result no fragment exists for index: 0x" + Integer.toHexString(index));
                } else {
                    active = FragmentHelper.getChildActive(fragment);
                }
            }
        }
        mFragmentRecord.clear();
        if (fragment == null) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showNotifyToast(String strNotify) {
        if (strNotify == null)
            return;

        if (mShowToast == null)
            mShowToast = Toast.makeText(getApplicationContext(), strNotify, Toast.LENGTH_SHORT);
        else
            mShowToast.setText(strNotify);
        mShowToast.show();
    }

    private void showNotifyToast(int strID) {
        if (mShowToast == null)
            mShowToast = Toast.makeText(getApplicationContext(), strID, Toast.LENGTH_SHORT);
        else
            mShowToast.setText(strID);
        mShowToast.show();
    }

    public final void asyncShowToast(final int strId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNotifyToast(strId);
            }
        });
    }

    public final void asyncShowToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNotifyToast(text);
            }
        });
    }

    public final void syncShowToast(int strId) {
        showNotifyToast(strId);
    }

    public final void syncShowToast(String text) {
        showNotifyToast(text);
    }

}
