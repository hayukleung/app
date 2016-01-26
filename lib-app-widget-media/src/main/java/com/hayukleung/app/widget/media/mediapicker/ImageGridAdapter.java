package com.hayukleung.app.widget.media.mediapicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hayukleung.app.BaseAdapter;
import com.hayukleung.app.widget.media.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter<Resource> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Resource> mSelectedData;
    private List<Resource> mResource = new ArrayList<>();

    public ImageGridAdapter(Activity context, ArrayList<Resource> data, List<Resource> selectedData, boolean showCamera) {
        super(context, data);
        this.showCamera = showCamera;
        this.mSelectedData = selectedData;
    }

    /**
     * 设置是否显示选择指示器
     *
     * @param showSelectIndicator
     */
    public void showSelectIndicator(boolean showSelectIndicator) {
        this.showSelectIndicator = showSelectIndicator;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 设置是否显示相机
     *
     * @param showCamera
     */
    public void setShowCamera(boolean showCamera) {
        if (this.showCamera == showCamera) return;

        this.showCamera = showCamera;
        notifyDataSetChanged();
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param resource
     */
    public void select(Resource resource) {
        if (mSelectedData.contains(resource)) {
            mSelectedData.remove(resource);
        } else {
            mSelectedData.add(resource);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String path : resultList) {
            Resource image = getImageByPath(path);
            if (image != null) {
                mSelectedData.add(image);
            }
        }
        if (mSelectedData.size() > 0) {
            notifyDataSetChanged();
        }
    }

    public void setData(List<Resource> data) {
        mResource.clear();
        mResource.addAll(data);
        notifyDataSetChanged();
    }

    private Resource getImageByPath(String path) {
        if (mResource != null && mResource.size() > 0) {
            for (Resource image : mResource) {
                if (image.getFilename().equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return mResource.size() + (showCamera ? 1 : 0);
    }

    @Override
    public Resource getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mResource.get(i - 1);
        } else {
            return mResource.get(i);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if (type == TYPE_CAMERA) {
            view = mInflater.inflate(R.layout.item_camera, viewGroup, false);
            view.setTag(null);
        } else if (type == TYPE_NORMAL) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.item_image, viewGroup, false);
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (holder != null) {
                holder.bindData(getItem(i));
            }
        }

        return view;
    }

    class ViewHolder {
        ImageView image;
        View mask;
        ImageView indicator;

        ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            mask = view.findViewById(R.id.mask);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            view.setTag(this);
        }

        void bindData(final Resource data) {
            if (data == null) return;
            // 处理单选和多选状态
            if (showSelectIndicator) {
                if (mSelectedData.contains(data)) {
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.checkbox_red_checked);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    // 未选择
                    indicator.setImageResource(R.drawable.checkbox_unchecked);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.getFilePath());

            // 显示图片
            ImageLoader.Instance()
                    .load(imageFile)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.ic_image_black_48dp)
                    .fit()
                    .centerCrop()
                    .into(image);
        }
    }
}
