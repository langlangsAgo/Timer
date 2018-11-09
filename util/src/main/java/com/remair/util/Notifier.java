/**
 *
 */
package com.remair.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 处理弹出消息提示相关的事务
 *
 * @author Michael
 */
public class Notifier {

    /**
     * 通知是聊天消息时的消息id
     */
    static final int DURATION_SHORT = 1000;
    static final int DURATION_NORMAL = 3000;
    static final int DURATION_LONG = 7000;

    static Toast sToast = null;


    private Notifier() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 弹出较长时间的消息提示。持续时间：7s
     *
     * @param c 上下文
     * @param msg 消息内容
     */
    public static void showLongMsg(Context c, String msg) {
        toastShow(DURATION_LONG, msg);
        //showLong(c, msg);
    }


    /**
     * 弹出消息提示。持续时间：3s
     *
     * @param c 上下文
     * @param msg 消息内容
     */
    public static void showNormalMsg(Context c, String msg) {
        toastShow(DURATION_NORMAL, msg);
        //show(c, msg, DURATION_NORMAL);
    }


    /**
     * 弹出较短时间的消息提示。持续时间：1s
     *
     * @param c 上下文
     * @param msg 消息内容
     */
    public static void showShortMsg(Context c, String msg) {
        toastShow(DURATION_SHORT, msg);
    }


    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**
     * 自定义显示Toast时间
     */
    public static void show(Context context, CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }


    /**
     * 自定义显示Toast时间
     */
    public static void show(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }


    private static void toastShow(int duration, CharSequence msg) {
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = createToast(duration, msg);
        sToast.show();
    }


    private static Toast createToast(int duration, CharSequence msg) {
        return Toast.makeText(ContextUtils.getContext(), msg, duration);
    }
}
