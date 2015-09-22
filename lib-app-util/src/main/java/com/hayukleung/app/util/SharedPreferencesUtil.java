package com.hayukleung.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存共享的数据工具类
 * 
 */
public class SharedPreferencesUtil {
	private final String PREFERENCE_NAME = "cndatacom_shared_preferences";
	private SharedPreferences mSharedPreferences;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public SharedPreferencesUtil(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	/** 
	 * 保存字符串数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}

	/** 
	 * 获取字符串数据
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getString(String key, String... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getString(key, defValue[0]);
		else
			return mSharedPreferences.getString(key, "");

	}

	/** 
	 * 保存整型数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	/** 
	 * 获取整型数据
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);

	}

	/** 
	 * 保存布尔值数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveBoolean(String key, Boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	/** 
	 * 获取布尔值数据
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public Boolean getBoolean(String key, Boolean... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getBoolean(key, defValue[0]);
		else
			return mSharedPreferences.getBoolean(key, false);

	}

	/** 
	 * 保存整型数据
	 * 
	 * @param key
	 * @param value
	 */
	public void saveLong(String key, long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}

	/** 
	 * 获取整型数据
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public long getLong(String key, long defValue) {
		return mSharedPreferences.getLong(key, defValue);

	}

	/**
	 * 移出数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		return mSharedPreferences.edit().remove(key).commit();
	}

}
