/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rockerhieu.emojicon.emoji.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hayukleung.app.util.RegexConstant;
import com.hayukleung.app.util.ResUtil;
import com.hayukleung.app.util.ToastUtil;
import com.hayukleung.app.util.text.SpanUtils;
import com.hayukleung.app.util.text.URLSpan;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.regex.Pattern;

/**
 * TODO 如何在A模块调用B模块的界面？
 */
public class DemoActivity extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    EmojiconEditText mEditEmojicon;
    EmojiconTextView mTxtEmojicon;
    CheckBox mCheckBox;

    private ResUtil mResUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResUtil = new ResUtil(getPackageName());

        setContentView(mResUtil.getLayoutResId("activity_demo_emoji"));
        mEditEmojicon = (EmojiconEditText) findViewById(mResUtil.getIdResId("editEmojicon"));
        mTxtEmojicon = (EmojiconTextView) findViewById(mResUtil.getIdResId("txtEmojicon"));
        mTxtEmojicon.setMovementMethod(LinkMovementMethod.getInstance());
        mEditEmojicon.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 话题标签（系统Linkify）
//                Linkify.addLinks(mTxtEmojicon, Pattern.compile(RegexConstant.REG_CTX_LABEL, Pattern.CASE_INSENSITIVE), "http://www.baidu.com");
//                Linkify.addLinks(mTxtEmojicon, Pattern.compile(RegexConstant.REG_CTX_LABEL, Pattern.CASE_INSENSITIVE), "http://www.baidu.com", new Linkify.MatchFilter() {
//                    @Override
//                    public boolean acceptMatch(CharSequence s, int start, int end) {
//                        return !s.subSequence(start + 1, end - 1).toString().contains("#");
//                    }
//                }, null);
                // 自定义Linkify 需要 TextView.setMovementMethod(LinkMovementMethod.getInstance());
                Spannable spannable = new SpannableStringBuilder(s);
                SpanUtils.addStyle(spannable, Pattern.compile(RegexConstant.REG_CTX_LABEL), "", new URLSpan.SpanCreator() {
                    @Override
                    public URLSpan create(String url) {
                        return new URLSpan(
                                url,
                                getResources().getColor(android.R.color.holo_red_light),
                                getResources().getColor(android.R.color.black),
                                0,
                                true) {
                            @Override
                            public void onClick(View widget) {
                                ToastUtil.showToast(DemoActivity.this, ((TextView) widget).getText());
                            }
                        };
                    }
                });
                mTxtEmojicon.setText(spannable);
            }
        });
        mCheckBox = (CheckBox) findViewById(mResUtil.getIdResId("use_system_default"));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mEditEmojicon.setUseSystemDefault(b);
                mTxtEmojicon.setUseSystemDefault(b);
                setEmojiconFragment(b);
            }
        });

        setEmojiconFragment(false);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager().beginTransaction().replace(mResUtil.getIdResId("emojicons"), EmojiconsFragment.newInstance(useSystemDefault)).commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
    }
}
