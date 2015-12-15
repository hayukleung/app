package com.hayukleung.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hayukleung.app.widget.collapsible.CollapsibleAdapter;
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
public class MainActivity extends Activity {

    private CollapsibleView mCollapsibleView;
    private CollapsibleAdapter mCollapsibleAdapter;
    private List<Element> mAllElements = new ArrayList<>();
    private List<Element> mVisibleElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        if (null == savedInstanceState) {
            initData();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
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
            mCollapsibleAdapter.buildTree();
//            mCollapsibleAdapter.sortTree(mVisibleElements);
            mCollapsibleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("all", (Serializable) mAllElements);
        outState.putSerializable("visible", (Serializable) mVisibleElements);
    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        mCollapsibleView = (CollapsibleView) findViewById(R.id.ActivityMain$collapsible_view);
        mCollapsibleAdapter = new CollapsibleAdapter(MainActivity.this, mAllElements, mVisibleElements, new OnCollapsibleClickListener() {

            @Override
            public void onUsrClick(IElement usr, int position) {
                switch (usr.getId()) {
                    case "com.hayukleung.app.widget.collapsible.demo.TestCollapsibleActivity":
                        startActivity(new Intent(MainActivity.this, com.hayukleung.app.widget.collapsible.demo.TestCollapsibleActivity.class));
                        break;
                    case "com.hayukleung.app.util.screen.demo.ScreenDemoActivity":
                        startActivity(new Intent(MainActivity.this, com.hayukleung.app.util.screen.demo.ScreenDemoActivity.class));
                        break;
                    case "com.rockerhieu.emojicon.emoji.demo.EmojiDemoActivity":
                        startActivity(new Intent(MainActivity.this, com.rockerhieu.emojicon.emoji.demo.EmojiDemoActivity.class));
                        break;
                    case "com.hayukleung.app.util.text.demo.TextDemoActivity":
                        startActivity(new Intent(MainActivity.this, com.hayukleung.app.util.text.demo.TextDemoActivity.class));
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
        });
        mCollapsibleView.setAdapter(mCollapsibleAdapter);
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
        element = new Element(com.rockerhieu.emojicon.emoji.demo.EmojiDemoActivity.class.getName(), "emoji", false);
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);

        // 4级 ======================================================

        // 下面三行代码按顺序照抄
        mCollapsibleAdapter.buildTree();
        mCollapsibleAdapter.sortTree(mVisibleElements);
        mCollapsibleAdapter.notifyDataSetChanged();
    }
}
