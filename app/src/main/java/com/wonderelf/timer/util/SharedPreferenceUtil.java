package com.wonderelf.timer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.remair.util.LogUtils;
import com.wonderelf.timer.base.XApplication;

import java.util.Set;

public class SharedPreferenceUtil {

    private static final String DEFAULT_NAME = "nbvSharepre";

    private static SharedPreferences sharedPreferences;


    /**
     * 往默认的sp中写值
     */
    public static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }


    /**
     * 往指定的sp中写值
     */
    public static void putInt(String spName, String key, Integer value) {
        getSharedPreferences(spName).edit().putInt(key, value).apply();
    }


    /**
     * 从默认的sp中取值
     */
    public static int getInt(String key, int defauleValue) {
        try {
            return getSharedPreferences().getInt(key, defauleValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defauleValue;
        }
    }


    /**
     * 从指定的sp中取值
     */
    public static Integer getInt(String spName, String key, Integer defauleValue) {
        try {
            return getSharedPreferences(spName).getInt(key, defauleValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defauleValue;
        }
    }


    /**
     * 往默认的sp中写值
     */
    public static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }


    /**
     * 往指定的sp中写值
     */
    public static void putString(String spName, String key, String value) {
        getSharedPreferences(spName).edit().putString(key, value).apply();
    }


    /**
     * 从默认的sp中取值
     */
    public static String getString(String key, String defaultValue) {
        try {
            return getSharedPreferences().getString(key, defaultValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultValue;
        }
    }


    /**
     * 从指定的sp中取值
     */
    public static String getString(String spName, String key, String defaultValue) {
        try {
            return getSharedPreferences(spName).getString(key, defaultValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultValue;
        }
    }


    /**
     * 往默认的sp中写值
     */
    public static void putStringSet(String key, Set<String> value) {
        getSharedPreferences().edit().putStringSet(key, value).apply();
    }


    /**
     * 往指定的sp中写值
     */
    public static void putStringSet(String spName, String key, Set<String> value) {
        getSharedPreferences(spName).edit().putStringSet(key, value).apply();
    }


    /**
     * 从默认的sp中取值
     */
    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        try {
            return getSharedPreferences().getStringSet(key, defaultValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultValue;
        }
    }


    /**
     * 从指定的sp中取值
     */
    public static Set<String> getStringSet(String spName, String key, Set<String> defaultValue) {
        try {
            return getSharedPreferences(spName).getStringSet(key, defaultValue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultValue;
        }
    }


    /**
     * 从默认的sp中移除key值
     */
    public static void Remove(String key) {
        getSharedPreferences().edit().remove(key).apply();
    }


    /**
     * 从指定的sp中移除key值
     */
    public static void Remove(String spName, String key) {
        getSharedPreferences(spName).edit().remove(key).apply();
    }


    /**
     * 往默认的sp中存值
     */
    public static void putBoolean(String key, Boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }


    /**
     * 从默认的sp中取值
     */
    public static Boolean getBoolean(String key, Boolean defaultVlaue) {
        try {
            return getSharedPreferences().getBoolean(key, defaultVlaue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultVlaue;
        }
    }


    /**
     * 往默认的sp文件中存值
     */
    public static void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }


    /**
     * 往指定的sp文件中存值
     */
    public static void putLong(String spName, String key, long value) {
        getSharedPreferences(spName).edit().putLong(key, value).apply();
    }


    /**
     * 从默认的sp文件中取值
     */
    public static long getLong(String key, long defaultVlaue) {
        try {
            return getSharedPreferences().getLong(key, defaultVlaue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultVlaue;
        }
    }


    /**
     * 从指定的sp文件中取值
     */
    public static long getLong(String spName, String key, long defaultVlaue) {
        try {
            return getSharedPreferences(spName).getLong(key, defaultVlaue);
        } catch (ClassCastException e) {
            LogUtils.e(e);
            return defaultVlaue;
        }
    }


    /**
     * 往默认的sp文件中存值
     */
    public static void putFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).apply();
    }


    /**
     * 往指定的sp文件中存值
     */
    public static void putFloat(String spName, String key, float value) {
        getSharedPreferences(spName).edit().putFloat(key, value).apply();
    }


    /**
     * 从默认的sp文件中取值
     */
    public static float getFloat(String key, float defaultVlaue) {
        try {
            return getSharedPreferences().getFloat(key, defaultVlaue);
        } catch (Exception e) {
            LogUtils.e(e);
            return defaultVlaue;
        }
    }


    /**
     * 从指定的sp文件中取值
     */
    public static float getFloat(String spName, String key, float defaultVlaue) {
        try {
            return getSharedPreferences(spName).getFloat(key, defaultVlaue);
        } catch (ClassCastException exception) {
            LogUtils.e(exception);
            return defaultVlaue;
        }
    }


    /**
     * 获取默认的sp文件
     */
    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = XApplication.Companion.instance()
                    .getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }


    /**
     * 获取给定文件名的sp
     */
    public static SharedPreferences getSharedPreferences(String spName) {
        return XApplication.Companion.instance()
                .getSharedPreferences(spName, Context.MODE_PRIVATE);
    }


    /**
     * 获取布尔值
     *
     * @param spName sp文件名
     */
    public static boolean getBoolean(String spName, String key, boolean defaultValue) {
        try {
            return getSharedPreferences(spName).getBoolean(key, defaultValue);
        } catch (ClassCastException exception) {
            LogUtils.e(exception);
            return defaultValue;
        }
    }


    /**
     * 存储布尔值
     *
     * @param spName sp文件名
     */
    public static void putBoolean(String spName, String key, boolean value) {
        getSharedPreferences(spName).edit().putBoolean(key, value).apply();
    }
}
