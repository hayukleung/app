package com.hayukleung.app.widget.media.mediapicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayukleung.app.widget.media.R;

import java.util.List;

/**
 * 相册列表类
 *
 * @author Administrator
 */
public class ImageFolderAdapter extends BaseAdapter {
    private Context context;
    private List<Folder> list;

    public ImageFolderAdapter(Context context, List<Folder> list) {
        super();
        this.context = context;
        this.list = list;
    }

    public void changeData(List<Folder> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Folder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image_folder, null);
            holder = new ViewHolder();
            holder.dirItemIcon = (ImageView) convertView.findViewById(R.id.id_dir_choose);
            holder.dirItemName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
            holder.dirItemNum = (TextView) convertView.findViewById(R.id.id_dir_item_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.dirItemName.setText("所有图片");
        } else {
            if (getItem(position).name != null) {
                holder.dirItemName.setText(getItem(position).name);
            }
        }
        holder.dirItemNum.setText("(" + getItem(position).images.size() + ")");
//        if (list.get(position).isSelected())
        holder.dirItemIcon.setVisibility(View.VISIBLE);
//        else
//            holder.dirItemIcon.setVisibility(View.INVISIBLE);
        return convertView;
    }

    static class ViewHolder {
        TextView dirItemName;
        TextView dirItemNum;
        ImageView dirItemIcon;
    }
}
