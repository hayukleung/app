package com.hayukleung.app.widget.media.mediapicker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hayukleung.app.Activities;
import com.hayukleung.app.CommonFragment;
import com.hayukleung.app.screen.Screen;
import com.hayukleung.app.util.ToastUtil;
import com.hayukleung.app.view.refresh.SwipeRefreshLayout;
import com.hayukleung.app.view.util.AndroidUtils;
import com.hayukleung.app.widget.media.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 选择页面
 */
public class MediaSelectFragment extends CommonFragment {
    /**
     * 选择结果，返回为 {@link ArrayList}&lt;{@link Resource}&gt; 或 {@link Resource}
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 最大图片选择次数，int类型
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，int类型
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，boolean类型
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 默认选择的数据集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;
    /**
     * 剪切
     */
    public static final int MODE_CROP = 2;
    public static final int MODE_CAMERA = 3;

    public static int PICTRUE_LIMIT_SIZE = 9;

    static final String PREVIEW_LIST = "preview_list";
    static final String PREVIEW_SELECTED_LIST = "preview_selected_list";
    static final String CROP_DATA = "crop_data";

    // Save when recycled
    private static final String CAMERA_FILE_PATH = "camera_file_path";
    private static final String CROP_FILE_PATH = "crop_file_path";

    /**
     * 请求加载系统照相机
     */
    private static final int REQUEST_CAMERA = 100;
    /**
     * 预览
     */
    private static final int REQUEST_PREVIEW = 101;
    /**
     * 剪切
     */
    private static final int REQUEST_CROP = 102;

    public static final String EXTRA_CROP_ASPECTX = "crop_aspectx";
    public static final String EXTRA_CROP_ASPECTY = "crop_aspecty";
    public static final String EXTRA_CROP_OUTPUTX = "crop_outputx";
    public static final String EXTRA_CROP_OUTPUTY = "crop_outputy";

    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    private GridView mList;
    private TextView mPreview;
    private TextView mComplete;
    private View mCountLayout;
    private TextView mCount;
    private View mImageFolderLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView mTakePhoto;

    // 结果数据
    private ArrayList<Resource> mSelectedResources;
    // 文件夹数据
    private ArrayList<Folder> mFolders = new ArrayList<>();
    private Folder mCurrentFolder;
    private ArrayList<Resource> mResources = new ArrayList<>();

    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir = new File("");
    /**
     * 所有图片
     */
    private List<String> mImages = new ArrayList<>();
    private ImageFolderAdapter mImageFolderAdapter;
    private FolderAdapter mFolderAdapter;

    private int mMode = MODE_SINGLE;
    private boolean mIsShowCamera = true;

    private TextView mHeaderTitle;
    private ImageView mHeaderIcon;
    private RelativeLayout mHeaderLeft;
    private LinearLayout mHeaderCenter;
    private ListView mDirListView;
    private View mImageFolderBackground;

    /**
     * 是否从相机返回
     */
    private boolean mIsFromCameraBack = false;

    private ImageGridAdapter mMediaSelectAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                ArrayList<Folder> folders = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        String mimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        Resource resource = new Resource(path, name, 0, 0, new File(path).length(), dateTime, mimeType, false);

                        // 获取文件夹名称
                        File imageFile = new File(resource.getFilePath());
                        File folderFile = imageFile.getParentFile();
                        if (folderFile != null) {
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            if (!folders.contains(Folder.FOLDER_ALL)) {
                                Folder folderAll = new Folder();
                                List<Resource> resourceList = new ArrayList<>();
                                resourceList.add(resource);
                                folderAll.images = resourceList;
                                folders.add(folderAll);
                            } else {
                                Folder f = folders.get(folders.indexOf(Folder.FOLDER_ALL));
                                f.images.add(resource);
                            }
                            if (!folders.contains(folder)) {
                                List<Resource> resourceList = new ArrayList<>();
                                resourceList.add(resource);
                                folder.images = resourceList;
                                folders.add(folder);
                            } else {
                                // 更新
                                Folder f = folders.get(folders.indexOf(folder));
                                f.images.add(resource);
                            }
                        }
                    } while (data.moveToNext());

                    mFolders.clear();
                    mFolders.addAll(folders);
                    if (mCurrentFolder != null) {
                        mCurrentFolder = mFolders.get(mFolders.indexOf(mCurrentFolder));
                    } else {
                        mCurrentFolder = mFolders.get(mFolders.indexOf(Folder.FOLDER_ALL));
                    }
                    mResources.clear();
                    mResources.addAll(mCurrentFolder.images);
                    mMediaSelectAdapter.setData(mCurrentFolder.images);
                    if (isViewCreated()) {
//                        mMediaSelectAdapter.notifyDataSetChanged();
                        mFolderAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    private File mCameraTmpFile;
    private File mCropTmpFile;
    private int mDesireImageCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(CAMERA_FILE_PATH);
            if (path != null) {
                mCameraTmpFile = new File(path);
            }
            path = savedInstanceState.getString(CROP_FILE_PATH);
            if (path != null) {
                mCropTmpFile = new File(path);
            }
        }

        // 图片选择模式
        mMode = getArguments().getInt(EXTRA_SELECT_MODE, MODE_SINGLE);

        // 选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        if (mMode == MODE_MULTI) {
            mSelectedResources = (ArrayList<Resource>) getArguments().getSerializable(EXTRA_DEFAULT_SELECTED_LIST);
        } else if (mMode == MODE_CAMERA) {
            showCameraAction();
        }
        if (mSelectedResources == null) {
            mSelectedResources = new ArrayList<>();
        }

        // 是否显示照相机
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);

        getLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_media_select, parent, false);

        mList = (GridView) mContentView.findViewById(R.id.list);
        mPreview = (TextView) mContentView.findViewById(R.id.preview);
        mComplete = (TextView) mContentView.findViewById(R.id.complete);
        mCountLayout = mContentView.findViewById(R.id.count_layout);
        mCount = (TextView) mContentView.findViewById(R.id.count);
        mImageFolderLayout = mContentView.findViewById(R.id.image_folder_layout);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.refresh_layout);
        mTakePhoto = (ImageView) mContentView.findViewById(R.id.take_photo);

        mHeaderTitle = (TextView) mContentView.findViewById(R.id.header_title);
        mHeaderIcon = (ImageView) mContentView.findViewById(R.id.header_icon);
        mHeaderLeft = (RelativeLayout) mContentView.findViewById(R.id.header_left);
        mHeaderCenter = (LinearLayout) mContentView.findViewById(R.id.header_center);
        mDirListView = (ListView) mContentView.findViewById(R.id.id_list_dirs);
        mImageFolderBackground = mContentView.findViewById(R.id.image_folder_background);

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mRefreshLayout.setRefreshing(false);
//                getLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
            }

            @Override
            public void onProgress(float progress) {
//                mHeader.setTriggerProgress(progress);
            }
        });
//        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHeaderTitle.setText("所有图片");
        mHeaderCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initListDirPopupWindow();
            }
        });
        mHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        updateUI();
        mMediaSelectAdapter = new ImageGridAdapter(getActivity(), mResources, mSelectedResources, mIsShowCamera);
        mFolderAdapter = new FolderAdapter(getActivity(), mFolders);
        mList.setAdapter(mMediaSelectAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mMediaSelectAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i == 0) {
                        showCameraAction();
                    } else {
                        // 正常操作
                        Resource image = (Resource) adapterView.getAdapter().getItem(i);
                        image.setIsSelected(true);
                        selectResource(image);
                    }
                } else {
                    // 正常操作
                    Resource image = (Resource) adapterView.getAdapter().getItem(i);
                    image.setIsSelected(true);
                    selectResource(image);
                }
            }
        });

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAction();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mList = null;
        mPreview = null;
        mComplete = null;
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(LOADER_ALL);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 判断选择数量问题
        if (mMode == MODE_MULTI && mDesireImageCount == mSelectedResources.size()) {
            ToastUtil.showToast(mActivity, getString(R.string.msg_amount_limit, mDesireImageCount));
            return;
        }
        // 跳转到系统照相机
        try {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mCameraTmpFile = FileUtils.getTmpFile();
            Intent intent = AndroidUtils.getImageFromCamera(mCameraTmpFile);
            Activities.startActivity(this, intent, REQUEST_CAMERA);

        } catch (ActivityNotFoundException e) {
            ToastUtil.showToast(mActivity, R.string.msg_no_camera);
        } catch (Exception e) {
            ToastUtil.showToast(mActivity, R.string.msg_no_sdcard);
        }
    }

    /**
     * 选择图片操作
     *
     * @param resource
     */
    private void selectResource(Resource resource) {
        if (resource != null) {
            // 多选模式
            if (mMode == MODE_MULTI) {
                if (mSelectedResources.contains(resource)) {
                    mSelectedResources.remove(resource);
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == mSelectedResources.size()) {
                        ToastUtil.showToast(mActivity, getString(R.string.msg_amount_limit, mDesireImageCount));
                        return;
                    }
                    resource.setIsSelected(true);
                    mSelectedResources.add(resource);
                }
                updateUI();
                mMediaSelectAdapter.notifyDataSetChanged();
            } else if (mMode == MODE_SINGLE) {
                Intent data = new Intent();
                data.putExtra(EXTRA_RESULT, resource);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().onBackPressed();
            } else if (mMode == MODE_CROP) {
                try {
                    Uri data = Uri.fromFile(new File(resource.getFilePath()));
                    mCropTmpFile = FileUtils.getTmpFile();
                    Uri out = Uri.fromFile(mCropTmpFile);
                    int aspectX = getArguments().getInt(EXTRA_CROP_ASPECTX);
                    int aspectY = getArguments().getInt(EXTRA_CROP_ASPECTY);
                    int outputX = getArguments().getInt(EXTRA_CROP_OUTPUTX);
                    int outputY = getArguments().getInt(EXTRA_CROP_OUTPUTY);
                    Intent intent = AndroidUtils.getImageFromCrop(data, out, aspectX, aspectY, outputX, outputY);
                    Activities.startActivity(this, intent, REQUEST_CROP);
                } catch (ActivityNotFoundException e) {
                    ToastUtil.showToast(mActivity, R.string.msg_no_crop);
                } catch (Exception e) {
                    ToastUtil.showToast(mActivity, R.string.msg_no_sdcard);
                }
            }
        }
    }

    // TODO
    private static final int DURATION = 600;
    private AnimatorSet mAnimatorSetVisible;
    private AnimatorSet mAnimatorSetGone;
    private static final int DURATION_CLOCKWISE = 400;
    private Animator mAnimatorClockwise;
    private Animator mAnimatorAntiClockwise;

    /**
     * 初始化展示文件夹的popupWindow
     */
    private void initListDirPopupWindow() {

        toggleDirList();

        if (null == mImageFolderAdapter) {
            mImageFolderAdapter = new ImageFolderAdapter(mActivity, mFolders);
            mDirListView.setAdapter(mImageFolderAdapter);
            mDirListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
//                        mHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                        if (position == 0) {
                            mHeaderTitle.setText("所有图片");
                        } else {
                            mHeaderTitle.setText(mFolders.get(position).name);
                        }
                        if (position != 0) {
                            mImgDir = new File(mFolders.get(position).path);
                            mImages = Arrays.asList(mImgDir.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
                                        return true;
                                    }
                                    return false;
                                }
                            }));
                        }
                        mImageFolderAdapter.changeData(mFolders);
                        List<Resource> typeResources = new ArrayList<Resource>();
                        if (position != 0) {
                            for (Resource resource : mResources) {
                                for (String filename : mImages) {
                                    if (resource.getFilename().equals(filename)) {
                                        typeResources.add(resource);
                                    }
                                }
                            }
                            mMediaSelectAdapter.setData(typeResources);
                        } else {
                            mMediaSelectAdapter.setData(mResources);
                        }
//                        mImageFolderLayout.setVisibility(View.GONE);
//                        mHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                        toggleDirList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mImageFolderBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mImageFolderLayout.setVisibility(View.GONE);
//                    mHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                    toggleDirList();
                }
            });
        }
    }

    private Screen mScreen;

    private int getDirListAnimationTranslationY() {
        if (null == mScreen) {
            mScreen = Screen.getInstance(mActivity);
        }
        return -Math.min(
                mFolders.size() * getResources().getDimensionPixelSize(R.dimen.xp12_0) + mFolders.size() - 1,
                mScreen.heightPx - mScreen.getStatusBarHeight(mActivity) - 2 * getResources().getDimensionPixelSize(R.dimen.header_height));
    }

    private void toggleDirList() {
        if (mImageFolderLayout.getVisibility() == View.VISIBLE) {
            if (null == mAnimatorSetGone) {
                mAnimatorSetGone = new AnimatorSet();
                mAnimatorSetGone.play(ObjectAnimator.ofFloat(mDirListView, "translationY", 0f, getDirListAnimationTranslationY()).setDuration(DURATION));
                mAnimatorSetGone.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mImageFolderLayout.setVisibility(View.GONE);
                        if (null == mAnimatorClockwise) {
                            mAnimatorClockwise = ObjectAnimator.ofFloat(mHeaderIcon, "rotation", 0, 180);
                            mAnimatorClockwise.setDuration(DURATION_CLOCKWISE);
                            mAnimatorClockwise.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mHeaderIcon.setRotation(0);
                                    mHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                        }
                        mAnimatorClockwise.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
            mDirListView.setPivotX(getResources().getDimensionPixelSize(R.dimen.xp50_0));
            mAnimatorSetGone.start();
            return;
        } else {
            if (null == mAnimatorSetVisible) {
                mAnimatorSetVisible = new AnimatorSet();
                mAnimatorSetVisible.play(ObjectAnimator.ofFloat(mDirListView, "translationY", getDirListAnimationTranslationY(), 0f).setDuration(DURATION));
                mAnimatorSetVisible.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDirListView.setTranslationY(getDirListAnimationTranslationY());
                        mImageFolderLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (null == mAnimatorAntiClockwise) {
                            mAnimatorAntiClockwise = ObjectAnimator.ofFloat(mHeaderIcon, "rotation", 0, -180);
                            mAnimatorAntiClockwise.setDuration(DURATION_CLOCKWISE);
                            mAnimatorAntiClockwise.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mHeaderIcon.setRotation(0);
                                    mHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_up_black_18dp);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                        }
                        mAnimatorAntiClockwise.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
            mDirListView.setPivotX(getResources().getDimensionPixelSize(R.dimen.xp50_0));
            mAnimatorSetVisible.start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCameraTmpFile != null) {
            outState.putString(CAMERA_FILE_PATH, mCameraTmpFile.getAbsolutePath());
        }
        if (mCropTmpFile != null) {
            outState.putString(CROP_FILE_PATH, mCropTmpFile.getAbsolutePath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMode == MODE_CAMERA && resultCode != Activity.RESULT_OK) {
            // 如果选择拍照，取消拍照，则直接返回
            getActivity().onBackPressed();
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                mIsFromCameraBack = true;
                if (mMode == MODE_CAMERA) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_RESULT, new Resource(mCameraTmpFile.getAbsolutePath()));
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().onBackPressed();
                } else {
                    AndroidUtils.insertImage(mActivity, mCameraTmpFile.getAbsolutePath());
                    selectResource(new Resource(mCameraTmpFile.getAbsolutePath()));
                    mCameraTmpFile = null;
                }
                // 拍完照片直接返回
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                mSelectedResources.add(new Resource(mCameraTmpFile.getAbsolutePath()));
//                bundle.putSerializable(EXTRA_RESULT, mSelectedResources);
//                intent.putExtras(bundle);
//                getActivity().setResult(Activity.RESULT_OK, intent);
//                getActivity().onBackPressed();
                return;
            } else if (requestCode == REQUEST_CROP) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_RESULT, new Resource(mCropTmpFile.getAbsolutePath()));
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().onBackPressed();
                return;
            } else if (requestCode == REQUEST_PREVIEW) {
                if (mSelectedResources != null && mSelectedResources.size() > 0) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EXTRA_RESULT, mSelectedResources);
                    intent.putExtras(bundle);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().onBackPressed();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        if (mMode != MODE_MULTI) {
            return;
        }
        int selectedCount = mSelectedResources.size();
        String previewText = getString(R.string.preview);
        mPreview.setTextColor(selectedCount != 0 ? mActivity.getResources().getColor(android.R.color.holo_red_light) : mActivity.getResources().getColor(android.R.color.darker_gray));
        mComplete.setTextColor(selectedCount != 0 ? mActivity.getResources().getColor(android.R.color.holo_red_light) : mActivity.getResources().getColor(android.R.color.darker_gray));
        mPreview.setEnabled(selectedCount != 0);
        mPreview.setText(previewText);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PREVIEW_LIST, mSelectedResources);
                bundle.putSerializable(PREVIEW_SELECTED_LIST, mSelectedResources);
                Activities.startActivity(MediaSelectFragment.this, PreviewFragment.class, bundle, REQUEST_PREVIEW);
            }
        });

        if (selectedCount > 0) {
            mCountLayout.setVisibility(View.VISIBLE);
            mCount.setText(selectedCount + "");
            if (mIsFromCameraBack) {
                mHeaderTitle.setText("所有图片");
            }
        } else {
            mCountLayout.setVisibility(View.GONE);
        }
        mComplete.setEnabled(selectedCount != 0);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理点击数据统计
//                Analysis.onEvent(mActivity, "postPhoto");
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_RESULT, mSelectedResources);
                data.putExtras(bundle);
                MediaSelectFragment.this.getActivity().setResult(Activity.RESULT_OK, data);
                MediaSelectFragment.this.getActivity().finish();
            }
        });
    }

    public static class SelectedResourcesSync {
        public Resource resource;
        public boolean isSelect;

        public SelectedResourcesSync(Resource data, boolean isSelect) {
            this.resource = data;
            this.isSelect = isSelect;
        }
    }

    /**
     * 选择图片同步事件
     */
    public void onEvent(SelectedResourcesSync selectedResourcesSync) {
        mIsFromCameraBack = false;
        if (selectedResourcesSync.isSelect) {
            if (!mSelectedResources.contains(selectedResourcesSync.resource)) {
                mSelectedResources.add(selectedResourcesSync.resource);
            }
        } else {
            mSelectedResources.remove(selectedResourcesSync.resource);
        }
        updateUI();
        if (mMediaSelectAdapter != null) {
            mMediaSelectAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected String getName() {
        return "选择页面";
    }
}
