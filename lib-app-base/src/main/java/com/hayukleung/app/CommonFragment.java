package com.hayukleung.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.hayukleung.app.base.R;
import com.hayukleung.app.view.Header;

public class CommonFragment extends BaseFragment {

    protected Activity mActivity;
    protected View mContentView;
    protected Header mHeader;
    protected Long TASK_TAG = SystemClock.elapsedRealtime();
    protected String mTempPath;
    protected String mCropPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        if (savedInstanceState != null) {
            mTempPath = savedInstanceState.getString("temp_path");
            mCropPath = savedInstanceState.getString("crop_path");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (Header) view.findViewById(R.id.header);
        setContentView(mContentView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("temp_path", mTempPath);
        outState.putString("crop_path", mCropPath);
    }

    @Override
    public void onDestroyView() {
        mContentView = null;
        mHeader = null;
        super.onDestroyView();
    }

    public void onResume() {
        super.onResume();
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
