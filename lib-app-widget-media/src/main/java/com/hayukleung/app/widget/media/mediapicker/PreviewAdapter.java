package com.hayukleung.app.widget.media.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hayukleung.app.Activities;
import com.hayukleung.app.view.RecyclingPagerAdapter;
import com.hayukleung.app.view.fullscreen.SystemUiHider;
import com.hayukleung.app.widget.media.R;
import com.hayukleung.app.widget.media.picasso.Callback;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PreviewAdapter extends RecyclingPagerAdapter {

    private Activity mActivity;
    private PreviewFragment mFragment;
    private List<Resource> mResources;
    private SystemUiHider mSystemUiHider;

    public PreviewAdapter(Activity activity, PreviewFragment previewFragment, List<Resource> resources, SystemUiHider systemUiHider) {
        this.mActivity = activity;
        this.mResources = resources;
        this.mFragment = previewFragment;
        this.mSystemUiHider = systemUiHider;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;
        if (convertView == null) {
            View view = mActivity.getLayoutInflater().inflate(R.layout.item_preview, container, false);
            holder = new ViewHolder(view);
            convertView = view;
            convertView.setTag(holder);
            holder.mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    mSystemUiHider.toggle();
                }
            });
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSystemUiHider.toggle();
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Resource resource = mResources.get(position);
        ImageLoader.Instance().load(new File(resource.getFilePath())).config(Bitmap.Config.RGB_565)
                .fit().centerInside()
                .into(holder.mImage, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        holder.mAttacher.update();
                    }
                });
        holder.mPlay.setVisibility(resource.isVideo() ? View.VISIBLE : View.GONE);
        holder.mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(resource.getFilePath())), resource.getMimeType());
                Activities.startActivity(mActivity, intent);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    static class ViewHolder {
        PhotoViewAttacher mAttacher;
        ImageView mImage;
        ImageView mPlay;

        ViewHolder(View view) {
            mImage = (ImageView) view.findViewById(R.id.image);
            mPlay = (ImageView) view.findViewById(R.id.play);
            mAttacher = new PhotoViewAttacher(mImage);
        }
    }
}
