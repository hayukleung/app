package com.hayukleung.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hayukleung.app.module.material.DemoMaterialActivity;
import com.hayukleung.app.module.qqLayout.DemoQQLayoutActivity;
import com.hayukleung.app.module.tabhost1.DemoTabHost1Activity;
import com.hayukleung.app.module.tabhost2.DemoTabHost2Activity;
import com.hayukleung.app.screen.Screen;
import com.hayukleung.app.screen.demo.DemoScreenActivity;
import com.hayukleung.app.snippet.handler_and_looper.DownloadQueueActivity;
import com.hayukleung.app.util.LogUtil;
import com.hayukleung.app.util.text.demo.DemoTextActivity;
import com.hayukleung.app.view.Header;
import com.hayukleung.app.widget.clock.AnalogClockView;
import com.hayukleung.app.widget.clock.demo.DemoClockActivity;
import com.hayukleung.app.widget.collapsible.CollapsibleView;
import com.hayukleung.app.widget.collapsible.Element;
import com.hayukleung.app.widget.collapsible.OnCollapsibleClickListener;
import com.hayukleung.app.widget.collapsible.demo.DemoCollapsibleActivity;
import com.hayukleung.app.widget.media.mediapicker.MediaSelectFragment;
import com.hayukleung.app.widget.paintpad.demo.DemoPaintPadActivity;
import com.hayukleung.app.widget.qrcode.demo.DemoQRCodeActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hayukleung on 15/9/3.
 */
public class MainActivity extends CommonActivity {

    @InjectView(R.id.header)
    public Header mHeader;
    @InjectView(R.id.ActivityMain$collapsible_view)
    public CollapsibleView mCollapsibleView;

    private ArrayList<Element> mAllElements = new ArrayList<>();
    private ArrayList<Element> mVisibleElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
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

    @Override
    protected void onDestroy() {
        ButterKnife.reset(this);
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        AnalogClockView analogClockView = new AnalogClockView(MainActivity.this);
//        ViewGroup.LayoutParams params = analogClockView.getLayoutParams();
//        params.height = getResources().getDimensionPixelSize(R.dimen.header_button_width);
//        params.width = params.height;
//        analogClockView.setLayoutParams(params);
        mHeader.setLeftView(analogClockView, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoClockActivity.class));
            }
        });
        mHeader.setCenterText(R.string.app_name, null);

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

        Element elementModule = new Element("com.hayukleung.app.module", "Module", true) {
            @Override
            public void onElementClick() {
            }
        };
        elementModule.setParentId(rootElement.getId());
        mAllElements.add(elementModule);

        Element elementSnippet = new Element("com.hayukleung.app.snippet", "Snippet", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementSnippet.setParentId(rootElement.getId());
        mAllElements.add(elementSnippet);

        Element elementUtil = new Element("com.hayukleung.app.util", "Util", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementUtil.setParentId(rootElement.getId());
        mAllElements.add(elementUtil);

        Element elementWidget = new Element("com.hayukleung.app.widget", "Widget", true) {

            @Override
            public void onElementClick() {

            }
        };
        elementWidget.setParentId(rootElement.getId());
        mAllElements.add(elementWidget);

        // 3级 ======================================================
        Element element;

        element = new Element(DemoMaterialActivity.class.getName(), "Material", false) {
            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoMaterialActivity.class));
            }
        };
        element.setParentId(elementModule.getId());
        mAllElements.add(element);
        element = new Element(DemoQQLayoutActivity.class.getName(), "QQLayout", false) {
            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoQQLayoutActivity.class));
            }
        };
        element.setParentId(elementModule.getId());
        mAllElements.add(element);
        element = new Element(DemoTabHost1Activity.class.getName(), "TabHost1", false) {
            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoTabHost1Activity.class));
            }
        };
        element.setParentId(elementModule.getId());
        mAllElements.add(element);
        element = new Element(DemoTabHost2Activity.class.getName(), "TabHost2", false) {
            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoTabHost2Activity.class));
            }
        };
        element.setParentId(elementModule.getId());
        mAllElements.add(element);

        element = new Element(DownloadQueueActivity.class.getName(), "Handler", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DownloadQueueActivity.class));
            }
        };
        element.setParentId(elementSnippet.getId());
        mAllElements.add(element);

        element = new Element(DemoScreenActivity.class.getName(), "Screen", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoScreenActivity.class));
            }
        };
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);
        element = new Element(DemoTextActivity.class.getName(), "Text", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoTextActivity.class));
            }
        };
        element.setParentId(elementUtil.getId());
        mAllElements.add(element);

        element = new Element(DemoCollapsibleActivity.class.getName(), "Collapsible", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoCollapsibleActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(DemoQRCodeActivity.class.getName(), "QRCode", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoQRCodeActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(DemoClockActivity.class.getName(), "Clock", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoClockActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(DemoPaintPadActivity.class.getName(), "PaintPad", false) {

            @Override
            public void onElementClick() {
                startActivity(new Intent(MainActivity.this, DemoPaintPadActivity.class));
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);
        element = new Element(MediaSelectFragment.class.getName(), "MediaPicker", false) {

            @Override
            public void onElementClick() {
                Bundle bundle = new Bundle();
                bundle.putInt(MediaSelectFragment.EXTRA_SELECT_MODE, MediaSelectFragment.MODE_MULTI);
                bundle.putBoolean(MediaSelectFragment.EXTRA_SHOW_CAMERA, false);
                bundle.putInt(MediaSelectFragment.EXTRA_SELECT_COUNT, 9);
                Activities.startActivity(MainActivity.this, MediaSelectFragment.class, bundle, 0x0001);
            }
        };
        element.setParentId(elementWidget.getId());
        mAllElements.add(element);

        // 4级 ======================================================

        // 下面三行代码按顺序照抄
        mCollapsibleView.buildTree().sortTree().notifyDataSetChanged();
    }
}
