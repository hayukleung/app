package com.hayukleung.app.util.text.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.hayukleung.app.util.R;
import com.hayukleung.app.util.RegexConstant;
import com.hayukleung.app.util.ToastUtil;
import com.hayukleung.app.util.text.SpanUtils;
import com.hayukleung.app.util.text.URLSpan;

import java.util.regex.Pattern;

/**
 * Created by hayukleung on 12/15/15.
 */
public class TextDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_text);

        TextView textPhone1 = (TextView) findViewById(R.id.txt_phone_1);
        TextView textPhone2 = (TextView) findViewById(R.id.txt_phone_2);
        TextView textWeb1 = (TextView) findViewById(R.id.txt_web_1);
        TextView textWeb2 = (TextView) findViewById(R.id.txt_web_2);
        TextView textSelfdef1 = (TextView) findViewById(R.id.txt_selfdef_1);
        TextView textSelfdef2 = (TextView) findViewById(R.id.txt_selfdef_2);

        textPhone1.setMovementMethod(LinkMovementMethod.getInstance());
        textPhone2.setMovementMethod(LinkMovementMethod.getInstance());
        textWeb1.setMovementMethod(LinkMovementMethod.getInstance());
        textWeb2.setMovementMethod(LinkMovementMethod.getInstance());
        textSelfdef1.setMovementMethod(LinkMovementMethod.getInstance());
        textSelfdef2.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable spannable;
        SpanUtils.addStyle(spannable = new SpannableString(textPhone1.getText()), Pattern.compile("(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}"), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan("tel:" + url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, true);
            }
        });
        textPhone1.setText(spannable);

        SpanUtils.addStyle(spannable = new SpannableString(textPhone2.getText()), Pattern.compile("(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}"), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan("tel:" + url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, false);
            }
        });
        textPhone2.setText(spannable);

        SpanUtils.addStyle(spannable = new SpannableString(textWeb1.getText()), Pattern.compile(RegexConstant.REG_CTX_URL), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan(url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, true);
            }
        });
        textWeb1.setText(spannable);

        SpanUtils.addStyle(spannable = new SpannableString(textWeb2.getText()), Pattern.compile(RegexConstant.REG_CTX_URL), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan(url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, false);
            }
        });
        textWeb2.setText(spannable);

        SpanUtils.addStyle(spannable = new SpannableString(textSelfdef1.getText()), Pattern.compile(RegexConstant.REG_CTX_LABEL), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan(url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, true) {
                    @Override
                    public void onClick(View widget) {
                        ToastUtil.showToast(TextDemoActivity.this, ((TextView) widget).getText());
                    }
                };
            }
        });
        textSelfdef1.setText(spannable);

        SpanUtils.addStyle(spannable = new SpannableString(textSelfdef2.getText()), Pattern.compile(RegexConstant.REG_CTX_LABEL), "", new URLSpan.SpanCreator() {
            @Override
            public URLSpan create(String url) {
                return new URLSpan(url, getResources().getColor(android.R.color.holo_blue_bright), 0, 0, false) {
                    @Override
                    public void onClick(View widget) {
                        ToastUtil.showToast(TextDemoActivity.this, ((TextView) widget).getText());
                    }
                };
            }
        });
        textSelfdef2.setText(spannable);
    }
}
