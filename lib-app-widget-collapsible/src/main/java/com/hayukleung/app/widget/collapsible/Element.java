package com.hayukleung.app.widget.collapsible;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 多级列表结点 </br> 类型： </br> TYPE_ORG 组织 可以拥有孩子结点 </br> TYPE_USR 人员 不可以拥有孩子结点 </br>
 *
 * @author HayukLeung
 */
public class Element implements IElement, Serializable, Comparable<IElement> {

    /**
     *
     */
    private static final long serialVersionUID = 7926360887921956233L;

    /**
     * 文字内容，可以替换为一个Java对象，比如UserInfo
     */
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 在tree中的层级
     */
    private int level = LEVEL_ROOT;

    @Override
    public int getLevel() {
        return level;
    }

    /**
     * 元素的ID
     */
    private String id = "";

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 父元素ID
     */
    private String parentId = "";

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 孩子元素
     */
    private List<Element> children = new ArrayList<Element>();

    @Override
    public void setChildren(List<Element> children) {
        if (TYPE_USR == this.type) {
            return;
        }
        if (TextUtils.isEmpty(this.id)) {
            throw new RuntimeException(EXCEPTION_ID);
        }
        if (null != children) {
            this.children.clear();
            this.children.addAll(children);
        }
        List<Element> tempElements = new ArrayList<Element>();
        // 遍历children
        for (Element child : this.children) {
            // 递归
            child.parentId = this.id;
            child.level = this.level + 1;
            if (child.hasChildren()) {
                tempElements.clear();
                tempElements.addAll(child.children);
                child.children.clear();
                child.setChildren(tempElements);
            }
        }
    }

    @Override
    public void addChild(Element child) {
        if (TYPE_USR == this.type) {
            // 如果本结点为人员类型结点，不应该拥有孩子结点
            return;
        }
        if (TextUtils.isEmpty(this.id)) {
            throw new RuntimeException(EXCEPTION_ID);
        }
        if (null != child && !children.contains(child)) {
            this.children.add(child);
            List<Element> tempElements = new ArrayList<Element>();
            child.parentId = this.id;
            child.level = this.level + 1;
            if (child.hasChildren()) {
                tempElements.clear();
                tempElements.addAll(child.children);
                child.children.clear();
                child.setChildren(tempElements);
            }
        }
    }

    @Override
    public void addChildren(List<Element> children) {
        if (TYPE_USR == this.type) {
            return;
        }
        if (null != children) {
            for (Element child : children) {
                addChild(child);
            }
        }
    }

    @Override
    public List<IElement> getChildrenCopy() {
        List<IElement> children = new ArrayList<IElement>();
        children.addAll(this.children);
        return children;
    }

    @Override
    public int getChildrenSize() {
        return children.size();
    }

    @Override
    public boolean hasChildren() {
        if (TYPE_USR == this.type && this.children.size() > 0) {
            throw new RuntimeException(EXCEPTION_CHILDREN_SIZE);
        }
        return children.size() > 0;
    }

    /**
     * 访问孩子结点集计数
     */
    private int accessChildrenNth = 1;

    /**
     * 访问孩子结点计数
     */
    private int accessChildNth = 1;

    @Override
    public void accessChildren(TraverseChildrenListener listener) {
        this.accessChildNth = 1;
        this.accessChildrenNth = 1;
        accessChildrenPrivate(listener);
    }

    /**
     * 遍历孩子结点，不包括孩子的孩子
     *
     * @param listener
     */
    private void accessChildrenPrivate(TraverseChildrenListener listener) {
        if (null == listener) {
            throw new RuntimeException(EXCEPTION_TRAVERSE_CHILDREN);
        }
        listener.doInTraverseChildren(this.children, this.accessChildrenNth++);
        for (Element child : this.children) {
            listener.doInTraverseChild(child, this.accessChildNth++);
        }
    }

    @Override
    public void accessChildrenRecursively(TraverseChildrenListener listener) {
        this.accessChildNth = 1;
        this.accessChildrenNth = 1;
        accessChildrenRecursivelyPrivate(listener);
    }

    /**
     * 递归遍历孩子结点，包括孩子的孩子
     *
     * @param listener
     */
    private void accessChildrenRecursivelyPrivate(TraverseChildrenListener listener) {
        if (null == listener) {
            throw new RuntimeException(EXCEPTION_TRAVERSE_CHILDREN);
        }
        listener.doInTraverseChildren(this.children, this.accessChildrenNth++);
        for (Element child : this.children) {
            listener.doInTraverseChild(child, this.accessChildNth++);
            if (child.hasChildren()) {
                child.accessChildrenRecursivelyPrivate(listener);
            }
        }
    }

    /**
     * item是否展开
     */
    private boolean isExpanded = false;

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    /**
     * 结点类型，默认人员结点
     */
    private Integer type = TYPE_USR;

    @Override
    public int getType() {
        return type;
    }

    /**
     * 优先级，数值越小，优先级越高
     */
    private Integer priority = 0;

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /** 常量 ********************************************************************/
    /**
     * 异常：ID
     */
    private final String EXCEPTION_ID = "本元素ID不能为空：" + this.toString();
    /**
     * 异常：孩子结点数目
     */
    private final String EXCEPTION_CHILDREN_SIZE = "人员元素孩子结点数目不应该大于0：" + this.toString();
    /**
     * 异常：遍历孩子结点监听
     */
    private final String EXCEPTION_TRAVERSE_CHILDREN = "遍历孩子结点监听未设置：" + this.toString();

    /**
     * 构造函数
     *
     * @param id
     * @param name
     * @param isOrg
     */
    public Element(String id, String name, boolean isOrg) {
        if (TextUtils.isEmpty(id)) {
            throw new RuntimeException(EXCEPTION_ID);
        }
        this.id = id;
        this.name = name;
        this.type = isOrg ? TYPE_ORG : TYPE_USR;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o || !(o instanceof IElement)) {
            return false;
        }
        // 由类型及ID唯一确定Element对象
        return this.id.equals(((IElement) o).getId()) && this.type == ((IElement) o).getType();
    }

    @Override
    public int compareTo(IElement another) {
        int result = 0;
        if (null != another) {
            result = this.type.compareTo(another.getType());
            if (0 == result) {
                result = this.priority.compareTo(another.getPriority());
                if (0 == result) {
                    result = this.name.compareTo(another.getName());
                }
            }
        }
        return result;
    }

}
