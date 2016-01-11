package com.hayukleung.app.widget.collapsible.demo;

import android.app.Activity;
import android.os.Bundle;

import com.hayukleung.app.util.ToastUtil;
import com.hayukleung.app.widget.collapsible.CollapsibleView;
import com.hayukleung.app.widget.collapsible.Element;
import com.hayukleung.app.widget.collapsible.IElement;
import com.hayukleung.app.widget.collapsible.OnCollapsibleClickListener;
import com.hayukleung.app.widget.collapsible.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayukleung on 15/9/3.
 */
public class DemoCollapsibleActivity extends Activity {

    private CollapsibleView mCollapsibleView;
    private List<Element> mAllElements = new ArrayList<>();
    private List<Element> mVisibleElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_collapsible);

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
            mCollapsibleView
                    .buildTree()
                    .notifyDataSetChanged();
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
        mCollapsibleView = (CollapsibleView) findViewById(R.id.collapsible_view);
        mCollapsibleView
                .setAllElements(mAllElements)
                .setVisibleElements(mVisibleElements)
                .setOnCollapsibleClickListener(new OnCollapsibleClickListener() {
                    @Override
                    public void onUsrClick(IElement usr, int position) {
                        ToastUtil.showToast(DemoCollapsibleActivity.this, usr.getName());
                    }

                    @Override
                    public boolean onOrgClick(IElement org, int position) {
                        return false;
                    }
                }).commit();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // TODO set mAllElements & mVisibleElements
        Element rootElement = new Element("0", "中华人民共和国", true);
        rootElement.setParentId("");
        mVisibleElements.add(rootElement);
        mAllElements.add(rootElement);
        // 大区 ======================================================
        Element element;
        element = new Element("11", "华北", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("12", "东北", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("13", "华东", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("14", "华南", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("15", "华中", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("16", "西南", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("17", "西北", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        element = new Element("18", "港澳台", true);
        element.setParentId(rootElement.getId());
        mAllElements.add(element);

        // 省区 ======================================================
        // 华北
        element = new Element("201", "北京市", false);
        element.setParentId("11");
        mAllElements.add(element);
        element = new Element("202", "天津市", false);
        element.setParentId("11");
        mAllElements.add(element);
        element = new Element("203", "河北省", false);
        element.setParentId("11");
        mAllElements.add(element);
        element = new Element("204", "山西省", false);
        element.setParentId("11");
        mAllElements.add(element);
        element = new Element("205", "内蒙古自治区", false);
        element.setParentId("11");
        mAllElements.add(element);
        // 东北
        element = new Element("206", "辽宁省", false);
        element.setParentId("12");
        mAllElements.add(element);
        element = new Element("207", "吉林省", false);
        element.setParentId("12");
        mAllElements.add(element);
        element = new Element("208", "黑龙江省", false);
        element.setParentId("12");
        mAllElements.add(element);
        // 华东
        element = new Element("209", "上海市", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("210", "江苏省", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("211", "浙江省", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("212", "安徽省", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("213", "福建省", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("214", "山西省", false);
        element.setParentId("13");
        mAllElements.add(element);
        element = new Element("215", "山东省", false);
        element.setParentId("13");
        mAllElements.add(element);
        // 华南
        element = new Element("216", "广东省", false);
        element.setParentId("14");
        mAllElements.add(element);
        element = new Element("217", "广西壮族自治区", false);
        element.setParentId("14");
        mAllElements.add(element);
        element = new Element("218", "海南省", false);
        element.setParentId("14");
        mAllElements.add(element);
        // 华中
        element = new Element("219", "河南省", false);
        element.setParentId("15");
        mAllElements.add(element);
        element = new Element("220", "湖北省", false);
        element.setParentId("15");
        mAllElements.add(element);
        element = new Element("221", "湖南省", false);
        element.setParentId("15");
        mAllElements.add(element);
        // 西南
        element = new Element("222", "重庆市", false);
        element.setParentId("16");
        mAllElements.add(element);
        element = new Element("223", "四川省", false);
        element.setParentId("16");
        mAllElements.add(element);
        element = new Element("224", "贵州省", false);
        element.setParentId("16");
        mAllElements.add(element);
        element = new Element("225", "云南省", false);
        element.setParentId("16");
        mAllElements.add(element);
        element = new Element("226", "西藏自治区", false);
        element.setParentId("16");
        mAllElements.add(element);
        // 西北
        element = new Element("227", "陕西省", false);
        element.setParentId("17");
        mAllElements.add(element);
        element = new Element("228", "甘肃省", false);
        element.setParentId("17");
        mAllElements.add(element);
        element = new Element("229", "青海省", false);
        element.setParentId("17");
        mAllElements.add(element);
        element = new Element("230", "宁夏回族自治区", false);
        element.setParentId("17");
        mAllElements.add(element);
        element = new Element("231", "新疆维吾尔自治区", false);
        element.setParentId("17");
        mAllElements.add(element);
        // 港澳台
        element = new Element("232", "香港特别行政区", false);
        element.setParentId("18");
        mAllElements.add(element);
        element = new Element("233", "澳门特别行政区", false);
        element.setParentId("18");
        mAllElements.add(element);
        element = new Element("234", "台湾省", false);
        element.setParentId("18");
        mAllElements.add(element);

        // 市区 ======================================================

        // 下面三行代码按顺序照抄
        mCollapsibleView
                .buildTree()
                .sortTree()
                .notifyDataSetChanged();
    }
}
