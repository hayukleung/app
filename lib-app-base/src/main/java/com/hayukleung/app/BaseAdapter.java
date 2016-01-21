package com.hayukleung.app;

import android.app.Activity;
import android.view.LayoutInflater;

import java.util.List;

public abstract class BaseAdapter<E> extends android.widget.BaseAdapter {
    protected final Activity mActivity;
    protected final LayoutInflater mInflater;
    protected final List<E> mData;

    public BaseAdapter(Activity activity, List<E> data) {
        this.mActivity = activity;
        this.mInflater = activity.getLayoutInflater();
        this.mData = data;
    }

    public void addData(List<E> data) {
        if (data == null) return;
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(List<E> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(E data) {
        if (data == null) return;
        mData.add(data);
        notifyDataSetChanged();
    }

    public void addData(int index, E data) {
        if (data == null) return;
        mData.add(index, data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public E getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
