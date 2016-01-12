package com.hayukleung.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hayukleung.app.snippet.handler_and_looper.DownloadQueueActivity;
import com.hayukleung.app.util.LogUtil;
import com.hayukleung.app.util.screen.Screen;
import com.hayukleung.app.util.screen.demo.DemoScreenActivity;
import com.hayukleung.app.util.text.demo.DemoTextActivity;
import com.hayukleung.app.view.Header;
import com.hayukleung.app.widget.clock.AnalogClockView;
import com.hayukleung.app.widget.clock.demo.DemoClockActivity;
import com.hayukleung.app.widget.collapsible.CollapsibleView;
import com.hayukleung.app.widget.collapsible.Element;
import com.hayukleung.app.widget.collapsible.OnCollapsibleClickListener;
import com.hayukleung.app.widget.collapsible.demo.DemoCollapsibleActivity;
import com.hayukleung.app.widget.qrcode.demo.DemoQRCodeActivity;

import java.util.ArrayList;

/**
 * Created by hayukleung on 15/9/3.
 */
public class MainActivity extends CommonActivity {

    private CollapsibleView mCollapsibleView;
    private ArrayList<Element> mAllElements = new ArrayList<>();
    private ArrayList<Element> mVisibleElements = new ArrayList<>();

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

        int[] screen = Screen.getScreenSize(MainActivity.this, true);
        LogUtil.showLog(String.format("width --> %d height --> %d", screen[0], screen[1]));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (0 == mAllElements.size()) {
//                mAllElements.addAll((ArrayList<Element>) savedInstanceState.getSerializable("all"));
                mAllElements = savedInstanceState.getParcelableArrayList("all");
            }
            if (0 == mVisibleElements.size()) {
//                mVisibleElements.addAll((ArrayList<Element>) savedInstanceState.getSerializable("visible"));
                mVisibleElements = savedInstanceState.getParcelableArrayList("visible");
            }
            mCollapsibleView.setAllElements(mAllElements).setVisibleElements(mVisibleElements).commit();
//            mCollapsibleView.buildTree().notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("all", (Serializable) mAllElements);
        outState.putParcelableArrayList("all", mAllElements);
//        outState.putSerializable("visible", (Serializable) mVisibleElements);
        outState.putParcelableArrayList("visible", mVisibleElements);
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        Header header = (Header) findViewById(R.id.header);
        AnalogClockView analogClockView = new AnalogClockView(MainActivity.this);
//        ViewGroup.LayoutParams params = analogClockView.getLayoutParams();
//        params.height = getResources().getDimensionPixelSize(R.dimen.header_button_width);
//        params.width = params.height;
//        analogClockView.setLayoutParams(params);
        header.setLeftView(analogClockView, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoClockActivity.class));
            }
        });
        header.setCenterText(R.string.app_name, null);

        mCollapsibleView = (CollapsibleView) findViewById(R.id.ActivityMain$collapsible_view);
        mCollapsibleView
                .setAllElements(mAllElements)
                .setVisibleElements(mVisibleElements)
                .setOnCollapsibleClickListener(new OnCollapsibleClickListener() {

                    @Override
                    public void onUsrClick(Element usr, int position) {
                        usr.onElementClick();
                    }

                    @Override
                    public boolean onOrgClick(Element org, int position) {
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
        Element rootElement = new Element(getPackageName(), getString(R.string.app_name), true) {

            @Override
            public void onElementClick() {

            }
        };
        rootElement.setParentId("");
        mVisibleElements.add(rootElement);
        mAllElements.add(rootElement);
        // 2级 ======================================================

        Element elementSnippet = new Element("com.hayukleung.app.snippet", "snippet", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementSnippet.setParentId(rootElement.getId());
        mAllElements.add(elementSnippet);

        Element elementUtil = new Element("com.hayukleung.app.util", "util", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementUtil.setParentId(rootElement.getId());
        mAllElements.add(elementUtil);

        Element elementWidget = new Element("com.hayukleung.app.widget", "widget", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementWidget.setParentId(rootElement.getId());
        mAllElements.add(elementWidget);

        // 3级 ======================================================
        Element element;

        element = new Element(DownloadQueueActivity.class.getName(), "handler", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DownloadQueueActivity.class));
            }
        };
        element.setParentId(elementSnippet.getId());
        mAllElements.add(element);

        element = new Element(DemoScreenActivity.class.getName(), "screen", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoScreenActivity.class));
            }
        };
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);
        element = new Element(DemoTextActivity.class.getName(), "text", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoTextActivity.class));
            }
        };
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);

        element = new Element(DemoCollapsibleActivity.class.getName(), "collapsible", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoCollapsibleActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(DemoQRCodeActivity.class.getName(), "qrcode", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoQRCodeActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(DemoClockActivity.class.getName(), "clock", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoClockActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);

        // 4级 ======================================================

        // 下面三行代码按顺序照抄
        mCollapsibleView.buildTree().sortTree().notifyDataSetChanged();
    }
}
