package com.hayukleung.app.util;

import android.app.Activity;

/**
 * SDK包资源读取方法 </br> 替换所有R.XXX.XXX的获取资源ID方法
 * 
 * @author HayukLeung
 * 
 */
public class ResUtil {

    private final String R_LAYOUT = "layout";
    private final String R_DRAWABLE = "drawable";
    private final String R_ID = "id";
    private final String R_ANIM = "anim";
    private final String R_RAW = "raw";
    private final String R_STRING = "string";
    private final String R_ATTR = "attr";
    private final String R_ARRAY = "array";
    private final String R_XML = "xml";
    private final String R_MENU = "menu";
    private final String R_DIMEN = "dimen";
    private final String R_STYLEABLE = "styleable";

    private String mPackageName;

    public ResUtil(Activity activity) {
        this.mPackageName = activity.getPackageName();
    }

    /**
     * 以反射方式获取R.layout资源ID
     * 
     * @param name
     * @return
     */
    public int getLayoutResId(String name) {
        return getResourseIdByName(R_LAYOUT, name);
    }

    /**
     * 以反射方式获取R.id资源ID
     * 
     * @param name
     * @return
     */
    public int getIdResId(String name) {
        return getResourseIdByName(R_ID, name);
    }

    /**
     * 以反射方式获取R.drawable资源ID
     * 
     * @param name
     * @return
     */
    public int getDrawableResId(String name) {
        return getResourseIdByName(R_DRAWABLE, name);
    }

    /**
     * 以反射方式获取R.anim资源ID
     * 
     * @param name
     * @return
     */
    public int getAnimResId(String name) {
        return getResourseIdByName(R_ANIM, name);
    }

    /**
     * 以反射方式获取R.raw资源ID
     * 
     * @param name
     * @return
     */
    public int getRawResId(String name) {
        return getResourseIdByName(R_RAW, name);
    }

    /**
     * 以反射方式获取R.string资源ID
     * 
     * @param name
     * @return
     */
    public int getStringResId(String name) {
        return getResourseIdByName(R_STRING, name);
    }

    /**
     * 以反射方式获取R.attr资源ID
     * 
     * @param name
     * @return
     */
    public int getAttrResId(String name) {
        return getResourseIdByName(R_ATTR, name);
    }

    /**
     * 以反射方式获取R.array资源ID
     * 
     * @param name
     * @return
     */
    public int getArrayResId(String name) {
        return getResourseIdByName(R_ARRAY, name);
    }

    /**
     * 以反射方式获取R.xml资源ID
     * 
     * @param name
     * @return
     */
    public int getXmlResId(String name) {
        return getResourseIdByName(R_XML, name);
    }

    /**
     * 以反射方式获取R.menu资源ID
     * 
     * @param name
     * @return
     */
    public int getMenuResId(String name) {
        return getResourseIdByName(R_MENU, name);
    }
    
    /**
     * 以反射方式获取R.dimen资源ID
     * 
     * @param name
     * @return
     */
    public int getDimenResId(String name) {
        return getResourseIdByName(R_DIMEN, name);
    }
    
    /**
     * 以反射方式获取R.styleable资源ID
     * 
     * @param name
     * @return
     */
    public int getStyleableResId(String name) {
        return getResourseIdByName(R_STYLEABLE, name);
    }

    /**
     * http://stackoverflow.com/questions/14373004/java-lang-noclassdeffounderror-com-facebook-android-rlayout-error-when-using-f
     * 
     * @param className
     * @param name
     * @return
     */
    private int getResourseIdByName(String className, String name) {

        LogUtil.showLog("getResourseIdByName --> " + mPackageName + " - " + className + " - " + name);

        Class<?> r = null;
        int id = 0;
        try {
            r = Class.forName(mPackageName + ".R");
            Class<?>[] classes = r.getClasses();
            Class<?> desireClass = null;
            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }
            if (desireClass != null)
                id = desireClass.getField(name).getInt(desireClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return id;
    }
}
