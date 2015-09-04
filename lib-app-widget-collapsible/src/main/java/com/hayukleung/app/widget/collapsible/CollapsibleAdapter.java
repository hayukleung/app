package com.hayukleung.app.widget.collapsible;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hayukleung.app.widget.collapsible.CollapsibleAdapter.CollapsibleViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collapsible适配器
 *
 * Created by administrator on 2015/8/26.
 */
public class CollapsibleAdapter extends CollapsibleAbstractAdapter<Element, CollapsibleViewHolder> {

    private OnCollapsibleClickListener mOnCollapsibleClickListener;

    private LayoutInflater mLayoutInflater;

    /** 元素数据源 */
    private List<Element> mAllElements;

    public List<Element> getAllElements() {
        return mAllElements;
    }

    /** 树中元素 */
    private List<Element> mVisibleElements;

    public List<Element> getVisibleElements() {
        return mVisibleElements;
    }

    /** 每一级的缩进量 */
    private final int mIndentBase;

    /**
     * 构造函数
     *
     * @param context
     * @param allElements
     * @param visibleElements
     * @param onCollapsibleClickListener
     */
    public CollapsibleAdapter(Context context, List<Element> allElements, List<Element> visibleElements, OnCollapsibleClickListener onCollapsibleClickListener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mAllElements = allElements;
        this.mVisibleElements = visibleElements;
        this.mOnCollapsibleClickListener = onCollapsibleClickListener;
        this.mIndentBase = (int) (((float) context.getResources().getDisplayMetrics().widthPixels) / 20);
    }


    @Override
    public CollapsibleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CollapsibleViewHolder(mLayoutInflater.inflate(R.layout.item_collapsible, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(CollapsibleViewHolder holder, final int position) {
        final IElement element = mVisibleElements.get(position);

        holder.txtName.setText(element.getName());

        if (IElement.TYPE_ORG == element.getType()) {
            // 组织类型
            if (element.hasChildren()) {
                // 有孩子结点
                holder.rlItem.setBackgroundResource(R.drawable.selector_tree_org);
                if (!element.isExpanded()) {
                    // 已闭合
                    holder.imgToggle.setImageResource(R.drawable.treeview_close);
                    holder.imgToggle.setVisibility(View.VISIBLE);
                } else {
                    // 已展开
                    holder.imgToggle.setImageResource(R.drawable.treeview_open);
                    holder.imgToggle.setVisibility(View.VISIBLE);
                }
            } else {
                // 没有孩子结点
                holder.rlItem.setBackgroundResource(R.drawable.selector_tree_org);
                holder.imgToggle.setImageResource(R.drawable.treeview_close);
                holder.imgToggle.setVisibility(View.INVISIBLE);
            }
            holder.rlItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (null != mOnCollapsibleClickListener) {
                        if (mOnCollapsibleClickListener.onOrgClick(element, position)) {
                            // 锁定本次点击
                        } else {
                            // 额外执行toggle
                            // mTreeViewAdapter.toggle(element, position);
                            toggleRecursively(element, position);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        } else {
            // 人员类型
            holder.rlItem.setBackgroundResource(R.drawable.selector_tree_usr);
            holder.imgToggle.setImageResource(R.drawable.treeview_close);
            holder.imgToggle.setVisibility(View.INVISIBLE);
            holder.rlItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (null != mOnCollapsibleClickListener) {
                        mOnCollapsibleClickListener.onUsrClick(element, position);
                    }
                }
            });
        }

        int level = element.getLevel();
        holder.rlItem.setPadding(
                mIndentBase * level,
                holder.rlItem.getPaddingTop(),
                holder.rlItem.getPaddingRight(),
                holder.rlItem.getPaddingBottom());
    }

    @Override
    public int getItemCount() {
        return null == mVisibleElements ? 0 : mVisibleElements.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void buildTree() {

        List<Element> temp = new ArrayList<Element>();
        temp.addAll(mAllElements);
        for (Element element1 : mAllElements) {
            temp.remove(element1);
            for (Element element2 : temp) {
                if (element2.getParentId() == element1.getId()) {
                    element1.addChild(element2);
                }
            }
            temp.add(element1);
        }
    }

    @Override
    public void sortTree(List<Element> tops) {

        if (null == tops || tops.size() <= 0) {
            return;
        }

        Collections.sort(tops);
        for (IElement element : tops) {
            element.accessChildrenRecursively(new IElement.TraverseChildrenListener() {

                @Override
                public void doInTraverseChild(Element child, int nth) {
                }

                @Override
                public void doInTraverseChildren(List<Element> children, int nth) {
                    Collections.sort(children);
                }
            });
        }
    }

    /**
     * 展开下一级孩子
     *
     * @param element
     * @param position
     */
    private void expand(IElement element, final int position) {

        // 展开操作
        element.setExpanded(true);
        element.accessChildren(new IElement.TraverseChildrenListener() {

            @Override
            public void doInTraverseChild(Element child, int nth) {
                mVisibleElements.add(position + nth, child);
                // LogMgr.showLog("expand " + child.getName() + " at position " + (position + nth));
            }

            @Override
            public void doInTraverseChildren(List<Element> children, int nth) {
            }
        });
    }

    /**
     * 闭合结点
     *
     * @param element
     * @param position
     */
    private void collapse(IElement element, int position) {

        // 闭合操作
        element.setExpanded(false);
        // 删除节点内部对应子节点数据，包括子节点的子节点
        ArrayList<IElement> elementsToDel = new ArrayList<IElement>();
        // 从position+1开始进行遍历
        for (int i = position + 1; i < mVisibleElements.size(); i++) {
            if (element.getLevel() >= mVisibleElements.get(i).getLevel())
                break;
            elementsToDel.add(mVisibleElements.get(i));
        }
        mVisibleElements.removeAll(elementsToDel);
    }

    @Override
    public void toggle(IElement element, int position) {

        if (IElement.TYPE_USR == element.getType()) {
            return;
        }

        if (element.isExpanded()) {
            collapse(element, position);
        } else {
            expand(element, position);
            element.accessChildren(new IElement.TraverseChildrenListener() {

                @Override
                public void doInTraverseChild(Element child, int nth) {
                    // 默认闭合
                    child.setExpanded(false);
                }

                @Override
                public void doInTraverseChildren(List<Element> children, int nth) {
                }
            });
        }
    }

    @Override
    public void toggleRecursively(IElement element, final int position) {

        if (IElement.TYPE_USR == element.getType()) {
            return;
        }

        if (element.isExpanded()) {
            collapse(element, position);
        } else {
            expand(element, position);
            // 这里不应该递归调用，第一层是toggle，接下来应该用refresh
            element.accessChildren(new IElement.TraverseChildrenListener() {

                @Override
                public void doInTraverseChild(Element child, int nth) {
                    // refreshRecursively(child, position + nth);
                    refreshRecursively(child, -1);
                }

                @Override
                public void doInTraverseChildren(List<Element> children, int nth) {
                }
            });
        }
    }

    /**
     * 用于刷新重新展开的结点的孩子的闭合状态
     *
     * @param element
     * @param position
     */
    private void refreshRecursively(IElement element, final int position) {

        if (IElement.TYPE_USR == element.getType()) {
            return;
        }

        if (element.isExpanded()) {

            final int start = mVisibleElements.indexOf(element);
            expand(element, start);
            element.accessChildren(new IElement.TraverseChildrenListener() {

                @Override
                public void doInTraverseChild(Element child, int nth) {
                    // refreshRecursively(child, start + nth);
                    refreshRecursively(child, -1);
                }

                @Override
                public void doInTraverseChildren(List<Element> children, int nth) {
                }
            });
        }
    }

    /**
     * 视图容器
     */
    static class CollapsibleViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlItem;
        ImageView imgToggle;
        TextView txtName;

        public CollapsibleViewHolder(View itemView) {
            super(itemView);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.ItemCollapsible$rl_item);
            imgToggle = (ImageView) itemView.findViewById(R.id.ItemCollapsible$img_toggle);
            txtName = (TextView) itemView.findViewById(R.id.ItemCollapsible$txt_name);
        }
    }
}
