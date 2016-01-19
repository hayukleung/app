package com.hayukleung.app.module.material;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayukleung.app.CommonFragment;
import com.hayukleung.app.R;

/**
 * TabAFragment.java
 * <p>
 * Created by hayukleung on 1/19/16.
 */
public class TabCFragment extends CommonFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.body_page_c, null, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
