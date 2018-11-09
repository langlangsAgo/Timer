package com.wonderelf.timer.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Author: cl
 * Time: 2018/11/8
 * Description:用于px和  dp，sp的转换工具
 */
public class PxUtils {

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(int sp,Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


}
