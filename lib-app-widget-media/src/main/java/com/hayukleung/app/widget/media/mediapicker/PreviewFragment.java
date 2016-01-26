package com.hayukleung.app.widget.media.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hayukleung.app.CommonFragment;
import com.hayukleung.app.view.fullscreen.SystemUiHider;
import com.hayukleung.app.widget.media.R;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 预览图片
 */
public class PreviewFragment extends CommonFragment {
    private ArrayList<Resource> mResources;
    private ArrayList<Resource> mSelectedResources;
    private SystemUiHider mSystemUiHider;

    private ViewPager mPager;
    private View mPreviewHeader;
    private RelativeLayout mHeaderLeft;
    private RelativeLayout mHeaderRight;
    private View mFooter;
    private CheckBox mSelected;
    private TextView mComplete;
    private TextView mImagePosition;
    private RelativeLayout mCountLayout;
    private TextView mCount;
    private int mShortAnimTime;
    private int mCurrentPosition = 0;
    private Resource mCurrentResource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResources = (ArrayList<Resource>) getArguments().getSerializable(MediaSelectFragment.PREVIEW_LIST);
        mSelectedResources = (ArrayList<Resource>) getArguments().getSerializable(MediaSelectFragment.PREVIEW_SELECTED_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_preview, container, false);

        mPager = (ViewPager) mContentView.findViewById(R.id.pager);
        mPreviewHeader = mContentView.findViewById(R.id.preview_header);
        mHeaderLeft = (RelativeLayout) mContentView.findViewById(R.id.header_left);
        mHeaderRight = (RelativeLayout) mContentView.findViewById(R.id.header_right);
        mFooter = mContentView.findViewById(R.id.footer);
        mSelected = (CheckBox) mContentView.findViewById(R.id.selected);
        mComplete = (TextView) mContentView.findViewById(R.id.complete);
        mImagePosition = (TextView) mContentView.findViewById(R.id.imagePosition);
        mCountLayout = (RelativeLayout) mContentView.findViewById(R.id.count_layout);
        mCount = (TextView) mContentView.findViewById(R.id.count);

        if (mResources != null) {
            mImagePosition.setText(1 + "/" + mResources.size());
            mCountLayout.setVisibility(View.VISIBLE);
            mCount.setText(mResources.size() + "");
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mHeader.setVisibility(View.GONE);
        mHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        mSystemUiHider = SystemUiHider.getInstance(mActivity, getContentView(), SystemUiHider.FLAG_HIDE_NAVIGATION);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChange(boolean visible) {
                if (mShortAnimTime == 0) {
                    mShortAnimTime = getResources().getInteger(
                            android.R.integer.config_shortAnimTime);
                }
                ObjectAnimator.ofFloat(mPreviewHeader, "translationY", visible ? 0 : -mPreviewHeader.getHeight())
                        .setDuration(mShortAnimTime).start();
                ObjectAnimator.ofFloat(mFooter, "translationY", visible ? 0 : mFooter.getHeight())
                        .setDuration(mShortAnimTime).start();
            }
        });
        PreviewAdapter adapter = new PreviewAdapter(mActivity, this, mResources, mSystemUiHider);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                if (mResources != null) {
                    mImagePosition.setText(position + 1 + "/" + mResources.size());
                }
                mCurrentResource = mResources.get(position);
                if (mCurrentResource.isSelected()) {
                    mSelected.setChecked(true);
                } else {
                    mSelected.setChecked(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPager.setAdapter(adapter);

        mSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentResource == null && mResources.size() > 0) {
                    mCurrentResource = mResources.get(0);
                }
                if (mCurrentResource.isSelected()) {
                    mResources.get(mCurrentPosition).setIsSelected(false);
                    mSelected.setChecked(false);
                } else {
                    mResources.get(mCurrentPosition).setIsSelected(true);
                    mSelected.setChecked(true);
                }
                EventBus.getDefault().post(new MediaSelectFragment.SelectedResourcesSync(mCurrentResource, mSelected.isChecked()));

                // 显示已经选择的图片数量
                showSelectedImageSize();
            }
        });

        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * 显示已经选择的图片数量
     */
    private void showSelectedImageSize() {
        if (mResources != null) {
            int selectedSize = 0;
            for (Resource resource : mResources) {
                if (resource.isSelected()) {
                    selectedSize++;
                }
            }
            if (selectedSize == 0) {
                mCountLayout.setVisibility(View.GONE);
                mComplete.setEnabled(false);
                mComplete.setTextColor(mActivity.getResources().getColor(android.R.color.darker_gray));
            } else {
                mCountLayout.setVisibility(View.VISIBLE);
                mCount.setText(selectedSize + "");
                mComplete.setEnabled(true);
                mComplete.setTextColor(mActivity.getResources().getColor(android.R.color.holo_red_light));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSystemUiHider = null;
        mPager = null;
        mFooter = null;
    }

    @Override
    protected String getName() {
        return "预览图片";
    }
}
