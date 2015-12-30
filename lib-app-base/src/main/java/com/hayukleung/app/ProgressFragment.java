/*
 * Copyright (C) 2013 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hayukleung.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hayukleung.app.base.R;

/**
 * The implementation of the fragment to display content. Based on {@link android.support.v4.app.ListFragment}. If you
 * are waiting for the initial data, you'll can displaying during this time an indeterminate progress indicator.
 *
 * @author Evgeny Shishkin
 */
public class ProgressFragment extends Fragment {

    private View mProgressContainer;
    private View mNetworkErrorContainer;
    private View mContentContainer;
    private View mContentView;
    private View mEmptyView;
    private View mEmptyContainer;
    private boolean mIsContentEmpty;
    private boolean mIsViewCreated;

    /**
     * Provide default implementation to return a simple view. Subclasses can override to replace with their own layout.
     * If doing so, the returned view hierarchy <em>must</em> have a progress container whose id is
     * {@link com.mdroid.R.id#progress_container R.id.progress_container}, content container whose id
     * is {@link com.mdroid.R.id#content_container R.id.content_container} and can optionally have a
     * sibling view id {@link android.R.id#empty android.R.id.empty} that is to be shown when the content is empty.
     * <p>
     * <p>
     * If you are overriding this method with your own custom content, consider including the standard layout
     * {@link com.mdroid.R.layout#fragment_progress} in your layout file, so that you continue to
     * retain all of the standard behavior of ProgressFragment. In particular, this is currently the only way to have
     * the built-in indeterminant progress state be shown.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    /**
     * Attach to view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureContent();
        mIsViewCreated = true;
    }

    /**
     * Detach from view.
     */
    @Override
    public void onDestroyView() {
        mIsViewCreated = false;
        mIsContentEmpty = false;
        mEmptyContainer = mNetworkErrorContainer = mProgressContainer = mContentContainer = mContentView = mEmptyView = null;
        super.onDestroyView();
    }

    /**
     * Return content view or null if the content view has not been initialized.
     *
     * @return content view or null
     * @see #setContentView(View)
     * @see #setContentView(int)
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * Set the content content from a layout resource.
     *
     * @param layoutResId Resource ID to be inflated.
     * @see #setContentView(View)
     * @see #getContentView()
     */
    public void setContentView(int layoutResId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View contentView = layoutInflater.inflate(layoutResId, null);
        setContentView(contentView);
    }

    /**
     * Set the content view to an explicit view. If the content view was installed earlier, the content will be replaced
     * with a new view.
     *
     * @param view The desired content to display. Value can't be null.
     * @see #setContentView(int)
     * @see #getContentView()
     */
    public void setContentView(View view) {
        ensureContent();
        if (view == null) {
            throw new IllegalArgumentException("Content view can't be null");
        }
        if (mContentContainer instanceof ViewGroup) {
            ViewGroup contentContainer = (ViewGroup) mContentContainer;
            ViewParent contentViewParent = view.getParent();
            if (contentViewParent == null) {
                if (mContentView == null) {
                    contentContainer.addView(view);
                } else {
                    int index = contentContainer.indexOfChild(mContentView);
                    // replace content view
                    contentContainer.removeView(mContentView);
                    contentContainer.addView(view, index);
                }
            } else if (contentViewParent != mContentContainer) {
                throw new IllegalArgumentException("Content view can't be in other ViewGroup");
            }
            mContentView = view;
        } else {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
    }

    /**
     * The default content for a ProgressFragment has a TextView that can be shown when the content is empty
     * {@link #setContentEmpty(boolean)}. If you would like to have it shown, call this method to supply the text it
     * should use.
     *
     * @param resId Identification of string from a resources
     * @see #setEmptyText(CharSequence)
     */
    public void setEmptyText(int resId) {
        setEmptyText(getString(resId));
    }

    /**
     * The default content for a ProgressFragment has a TextView that can be shown when the content is empty
     * {@link #setContentEmpty(boolean)}. If you would like to have it shown, call this method to supply the text it
     * should use.
     *
     * @param text Text for empty view
     * @see #setEmptyText(int)
     */
    public void setEmptyText(CharSequence text) {
        if (!mIsViewCreated) {
            return;
        }
        if (mEmptyView != null && mEmptyView instanceof TextView) {
            ((TextView) mEmptyView).setText(text);
        } else {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean animate) {
        if (!mIsViewCreated) {
            return;
        }
        if (mProgressContainer.getVisibility() == View.VISIBLE) {
            return;
        }
        View shownView = mContentContainer.getVisibility() == View.VISIBLE ? mContentContainer : mNetworkErrorContainer;
        View unshownView = mProgressContainer;
        switchView(shownView, unshownView, animate);
    }

    public void showError() {
        showError(true);
    }

    public void showError(boolean animate) {
        if (!mIsViewCreated) {
            return;
        }
        if (mNetworkErrorContainer.getVisibility() == View.VISIBLE) {
            return;
        }
        View shownView = mContentContainer.getVisibility() == View.VISIBLE ? mContentContainer : mProgressContainer;
        View unshownView = mNetworkErrorContainer;
        switchView(shownView, unshownView, animate);
    }

    public void showContent() {
        showContent(true);
    }

    /**
     * Control whether the content is being displayed. You can make it not displayed if you are waiting for the initial
     * data to show in it. During this time an indeterminant progress indicator will be shown instead.
     */
    public void showContent(boolean animate) {
        if (!mIsViewCreated) {
            return;
        }
        if (mContentContainer.getVisibility() == View.VISIBLE) {
            return;
        }
        View shownView = mNetworkErrorContainer.getVisibility() == View.VISIBLE ? mNetworkErrorContainer : mProgressContainer;
        View unshownView = mContentContainer;
        switchView(shownView, unshownView, animate);
    }

    private void switchView(View shownView, View unshownView, boolean animate) {
        if (animate) {
            shownView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            unshownView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        } else {
            shownView.clearAnimation();
            unshownView.clearAnimation();
        }
        shownView.setVisibility(View.GONE);
        unshownView.setVisibility(View.VISIBLE);
    }

    public void setNetworkErrorClickListener(View.OnClickListener listener) {
        mNetworkErrorContainer.setOnClickListener(listener);
    }

    public void setEmptyClickListener(View.OnClickListener listener) {
        mEmptyContainer.setOnClickListener(listener);
    }

    /**
     * Returns true if content is empty. The default content is not empty.
     *
     * @return true if content is null or empty
     * @see #setContentEmpty(boolean)
     */
    public boolean isContentEmpty() {
        return mIsContentEmpty;
    }

    /**
     * If the content is empty, then set true otherwise false. The default content is not empty. You can't call this
     * method if the content view has not been initialized before {@link #setContentView(View)} and content
     * view not null.
     *
     * @param isEmpty true if content is empty else false
     * @see #isContentEmpty()
     */
    public void setContentEmpty(boolean isEmpty) {
        if (!mIsViewCreated) {
            return;
        }
        if (mContentView == null) {
            throw new IllegalStateException("Content view must be initialized before");
        }
        if (isEmpty) {
            mEmptyContainer.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        } else {
            mEmptyContainer.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
        mIsContentEmpty = isEmpty;
    }

    public boolean isViewCreated() {
        return mIsViewCreated;
    }

    /**
     * Initialization views.
     */
    private void ensureContent() {
        if (mContentContainer != null && mProgressContainer != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mProgressContainer = root.findViewById(R.id.progress_container);
        if (mProgressContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");
        }
        mNetworkErrorContainer = root.findViewById(R.id.network_error_container);
        if (mNetworkErrorContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.network_error_container'");
        }
        mEmptyContainer = root.findViewById(R.id.empty_container);
        if (mEmptyContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.empty_container'");
        }
        mContentContainer = root.findViewById(R.id.content_container);
        if (mContentContainer == null) {
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
        }
        mEmptyView = root.findViewById(android.R.id.empty);
        // We are starting without a content, so assume we won't
        // have our data right away and start with the progress indicator.
        if (mContentView == null) {
            showContent(false);
        }
    }

}
