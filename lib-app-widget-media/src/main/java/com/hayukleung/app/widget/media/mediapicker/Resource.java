package com.hayukleung.app.widget.media.mediapicker;

import android.os.Parcel;

import java.io.Serializable;

public class Resource implements Comparable<Resource>, Serializable {
//    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
//        public Resource createFromParcel(Parcel source) {
//            return new Resource(source);
//        }
//
//        public Resource[] newArray(int size) {
//            return new Resource[size];
//        }
//    };
    private long mSize;
    private long mTime;
    private String mimeType;
    private String filename;
    private String filePath;
    private int width;
    private int height;

    /**
     * 传原图|压缩图片
     */
    private boolean mIsOriginal;
    private int mType;// 0: 图片 1: 视频
    private boolean isSelected;

    public Resource() {
    }

    public Resource(String path) {
        this.filePath = path;
    }

    public Resource(String path, String name, int width, int height, long size, long time, int type, String mimeType, boolean isSelected) {
        this.filePath = path;
        this.filename = name;
        this.width = width;
        this.height = height;
        this.mSize = size;
        this.mTime = time;
        this.mType = type;
        this.mimeType = mimeType;
        this.isSelected = isSelected;
    }

    /**
     * For video
     */
    public Resource(String path, String cover, String name, int width, int height, long size, long time, String mimeType) {
        this(path, name, width, height, size, time, 1, mimeType, false);
    }

    /**
     * For Image
     */
    public Resource(String path, String name, int width, int height, long size, long time, String mimeType, boolean isSelected) {
        this(path, name, width, height, size, time, 0, mimeType, isSelected);
    }

    protected Resource(Parcel in) {
        this.filePath = in.readString();
        this.filename = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mSize = in.readLong();
        this.mTime = in.readLong();
        this.mIsOriginal = in.readByte() != 0;
        this.mType = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean original() {
        return mIsOriginal;
    }

    public void setIsOriginal(boolean mIsOriginal) {
        this.mIsOriginal = mIsOriginal;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        return filePath.equals(resource.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }

    @Override
    public int compareTo(Resource another) {
        return (int) (mTime - another.mTime);
    }

//    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.filename);
        dest.writeLong(this.width);
        dest.writeLong(this.height);
        dest.writeLong(this.mSize);
        dest.writeLong(this.mTime);
        dest.writeByte(mIsOriginal ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mType);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    public boolean isVideo() {
        return mType == 1;
    }

    public String getMimeType() {
        return mimeType;
    }
}
