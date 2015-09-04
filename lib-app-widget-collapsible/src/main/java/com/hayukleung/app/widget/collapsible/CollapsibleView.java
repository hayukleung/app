package com.hayukleung.app.widget.collapsible;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by hayukleung on 15/9/3.
 */
public class CollapsibleView extends RecyclerView {

    public CollapsibleView(Context context) {
        super(context);
        init(context);
    }

    public CollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CollapsibleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        addItemDecoration(new DividerDecoration(context));
    }


}
