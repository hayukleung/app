package com.hayukleung.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.hayukleung.app.base.R;
import com.hayukleung.app.util.DirMgr;
import com.hayukleung.app.view.Header;

import java.io.File;
import java.math.BigDecimal;

public class CommonFragment extends com.hayukleung.app.BaseFragment {
    public static final int REQUEST_CODE_POST_ARTICLE_FINISH = 12;
    protected static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    protected static final int REQUEST_CODE_IMAGE_GALLERY = 2;
    protected static final int REQUEST_CODE_IMAGE_CROP = 3;
    /**
     * 私信选择图片
     */
    protected static final int REQUEST_CODE_IMAGE_GALLERY_MESSAGE = 4;
    /**
     * 选择视频
     */
    protected static final int REQUEST_CODE_VIDEO_MESSAGE = 5;
    protected static final int REQUEST_CODE_LOGIN_FINISH = 11;
    protected static final int REQUEST_CODE_USERINFO = 13;
    protected static final int REQUEST_CODE_CERTIFICAT_PHONE = 14;
    protected static final int REQUEST_CODE_CERTIFICAT_CAR_MASTOR = 15;
    protected static final int USER_FRAGMENT_BACK = 16;
    protected static final int REQUEST_CODE_IMAGE_CAMERA_JS = 17;//JS调native 拍照
    protected static final int REQUEST_CODE_IMAGE_GALLERY_JS = 18;//JS调native 相册
    protected static final int REQUEST_CODE_FINISH_CHARGING = 19;
    protected static final int REQUEST_LOGIN = 20;//请求登录界面
//    protected final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chargerlink/Portrait";
    protected final static String FILE_SAVEPATH = DirMgr.PATH_FILE;
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
//        HttpLoader.Instance().cancelAll(TASK_TAG);
//        LocalLoader.Instance().cancelAll(TASK_TAG);
        super.onDestroy();
    }

    protected void renderEditText(final EditText edit, final View del) {
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setText("");
            }
        });
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                del.setVisibility(editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                del.setVisibility(b && edit.getText().length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    /**
     * 选择单张图片
     *
     * @param isFromDiscover
     */
    public void getPhoto(final boolean isFromDiscover) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_MODE, MediaSelectFragment.MODE_SINGLE);
//        bundle.putBoolean(MediaSelectFragment.EXTRA_SHOW_CAMERA, false);
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_COUNT, 1);
//        Activities.startActivity(CommonFragment.this, MediaSelectFragment.class, bundle, isFromDiscover ? REQUEST_CODE_IMAGE_GALLERY_JS : REQUEST_CODE_IMAGE_GALLERY);
    }

    /**
     * 选择图片并裁剪
     */
    public void getPhotoByCrop() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_MODE, MediaSelectFragment.MODE_CROP);
//        bundle.putBoolean(MediaSelectFragment.EXTRA_SHOW_CAMERA, false);
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_COUNT, 1);
//        bundle.putInt(MediaSelectFragment.EXTRA_CROP_ASPECTX, 1);
//        bundle.putInt(MediaSelectFragment.EXTRA_CROP_ASPECTY, 1);
//        bundle.putInt(MediaSelectFragment.EXTRA_CROP_OUTPUTX, 400);
//        bundle.putInt(MediaSelectFragment.EXTRA_CROP_OUTPUTY, 400);
//        Activities.startActivity(CommonFragment.this, MediaSelectFragment.class, bundle, REQUEST_CODE_IMAGE_CROP);
    }

    /**
     * 选择多张图片
     *
     * @param bundle
     * @param maxSelectCount
     */
    public void getMultiPhoto(final Bundle bundle, final int maxSelectCount) {
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_MODE, MediaSelectFragment.MODE_MULTI);
//        bundle.putBoolean(MediaSelectFragment.EXTRA_SHOW_CAMERA, false);
//        bundle.putInt(MediaSelectFragment.EXTRA_SELECT_COUNT, maxSelectCount);
//        Activities.startActivity(CommonFragment.this, MediaSelectFragment.class, bundle, REQUEST_CODE_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            } else {
                file.delete();
            }
        }
    }

//    public long cacheSize() {
//        long size = 0;
//        size = getFileSize(ImageLoader.Instance().getDiskCache().getDirectory()) + getFileSize(new File(FILE_SAVEPATH));
//        return size;
//    }

    private long getFileSize(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        }
        try {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        size = size + getFileSize(fileList[i]);

                    } else {
                        size = size + fileList[i].length();

                    }
                }
            } else {
                size = file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

}
