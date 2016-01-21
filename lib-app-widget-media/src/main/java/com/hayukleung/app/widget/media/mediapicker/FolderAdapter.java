package com.hayukleung.app.widget.media.mediapicker;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hayukleung.app.view.recyclerView.RecyclerArrayAdapter;

import java.util.List;

public class FolderAdapter extends RecyclerArrayAdapter<Folder, RecyclerView.ViewHolder> {

    private List<Resource> mSelectedList;

    public FolderAdapter(Activity activity, List<Folder> data) {
        super(activity, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void setSelectedList(List<Resource> selectedList) {
        mSelectedList = selectedList;
        notifyDataSetChanged();
    }
}
