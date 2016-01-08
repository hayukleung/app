package com.hayukleung.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.hayukleung.app.util.LogUtil;
import com.hayukleung.app.widget.collapsible.CollapsibleView;
import com.hayukleung.app.widget.collapsible.Element;
import com.hayukleung.app.widget.collapsible.IElement;
import com.hayukleung.app.widget.collapsible.OnCollapsibleClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayukleung on 15/9/3.
 */
public class MainActivity extends CommonActivity {

    private CollapsibleView mCollapsibleView;
    private List<Element> mAllElements = new ArrayList<>();
    private List<Element> mVisibleElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//
//            // clear FLAG_TRANSLUCENT_STATUS flag:
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            // finally change the color
//            window.setStatusBarColor(getResources().getColor(R.color.base));
//        }
        initWidgets();

        if (null == savedInstanceState) {
            initData();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        LogUtil.showLog(String.format("width --> %d height --> %d", displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (0 == mAllElements.size()) {
                mAllElements.addAll((ArrayList<Element>) savedInstanceState.getSerializable("all"));
            }
            if (0 == mVisibleElements.size()) {
                mVisibleElements.addAll((ArrayList<Element>) savedInstanceState.getSerializable("visible"));
            }
            mCollapsibleView.buildTree().notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("all", (Serializable) mAllElements);
        outState.putSerializable("visible", (Serializable) mVisibleElements);
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        mCollapsibleView = (CollapsibleView) findViewById(R.id.ActivityMain$collapsible_view);
        mCollapsibleView
                .setAllElements(mAllElements)
                .setVisibleElements(mVisibleElements)
                .setOnCollapsibleClickListener(new OnCollapsibleClickListener() {

                    @Override
                    public void onUsrClick(IElement usr, int position) {
                        switch (usr.getId()) {
                            case "com.hayukleung.app.widget.collapsible.demo.TestCollapsibleActivity":
                                startActivity(new Intent(MainActivity.this, com.hayukleung.app.widget.collapsible.demo.TestCollapsibleActivity.class));
                                break;
                            case "com.hayukleung.app.util.screen.demo.ScreenDemoActivity":
                                startActivity(new Intent(MainActivity.this, com.hayukleung.app.util.screen.demo.ScreenDemoActivity.class));
                                break;
                            case "com.hayukleung.app.util.text.demo.TextDemoActivity":
                                startActivity(new Intent(MainActivity.this, com.hayukleung.app.util.text.demo.TextDemoActivity.class));
                                break;
                            case "com.hayukleung.app.widget.qrcode.demo.QRCodeDemoActivity":
                                startActivity(new Intent(MainActivity.this, com.hayukleung.app.widget.qrcode.demo.QRCodeDemoActivity.class));
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public boolean onOrgClick(IElement org, int position) {
                        if (!org.hasChildren()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
        }).commit();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // TODO set mAllElements & mVisibleElements
        Element rootElement = new Element(getPackageName(), getString(R.string.app_name), true);
        rootElement.setParentId("");
        mVisibleElements.add(rootElement);
        mAllElements.add(rootElement);
        // 2级 ======================================================

        Element elementUtil = new Element("com.hayukleung.app.util", "util", true);
        elementUtil.setParentId(rootElement.getId());
        mAllElements.add(elementUtil);

        Element elementWidget = new Element("com.hayukleung.app.widget", "widget", true);
        elementWidget.setParentId(rootElement.getId());
        mAllElements.add(elementWidget);

        // 3级 ======================================================
        Element element;

        element = new Element(com.hayukleung.app.util.screen.demo.ScreenDemoActivity.class.getName(), "screen", false);
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);
        element = new Element(com.hayukleung.app.util.text.demo.TextDemoActivity.class.getName(), "text", false);
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);

        element = new Element(com.hayukleung.app.widget.collapsible.demo.TestCollapsibleActivity.class.getName(), "collapsible", false);
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(com.hayukleung.app.widget.qrcode.demo.QRCodeDemoActivity.class.getName(), "qrcode", false);
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);

        // 4级 ======================================================

        // 下面三行代码按顺序照抄
        mCollapsibleView.buildTree().sortTree().notifyDataSetChanged();
    }
}
