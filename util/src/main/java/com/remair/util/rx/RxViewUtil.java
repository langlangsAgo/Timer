package com.remair.util.rx;

import android.support.annotation.Nullable;
import android.view.View;
import com.jakewharton.rxbinding.view.RxView;
import com.remair.util.LogUtils;

import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 项目名称：Android
 * 类描述：
 * 创建人：LiuJun
 * 创建时间：16/8/10 17:39
 * 修改人：LiuJun
 * 修改时间：16/8/10 17:39
 * 修改备注：
 */
public class RxViewUtil {

    @Nullable
    public static Subscription viewBindClick(final View view, final View.OnClickListener onClickListener) {
        if (nullCheck(view, onClickListener)) {
            return null;
        }
        return viewBindClick(view, 1, onClickListener);
    }


    /**
     * View绑定点击事件
     *
     * @param windowDuration 单位时间内,只响应一次点击事件。单位:秒
     */
    @Nullable
    public static Subscription viewBindClick(final View view, long windowDuration, final View.OnClickListener onClickListener) {
        if (nullCheck(view, onClickListener)) {
            return null;
        }
        return viewBindClick(view, windowDuration, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        });
    }


    @Nullable
    public static Subscription viewBindClick(View view, Action1<Void> action1) {
        return viewBindClick(view, 1, action1);
    }


    /**
     * View绑定点击事件
     *
     * @param windowDuration 单位时间内,只响应一次点击事件。单位:秒
     */
    @Nullable
    public static Subscription viewBindClick(View view, long windowDuration, Action1<Void> action1) {
        if (nullCheck(view, action1)) {
            return null;
        }
        return RxView.clicks(view)
                     .throttleFirst(windowDuration, TimeUnit.SECONDS)
                     .subscribe(action1, new Action1<Throwable>() {
                         @Override
                         public void call(Throwable throwable) {
                             LogUtils.e(throwable);
                         }
                     });
    }


    private static boolean nullCheck(View view, Object onClickListener) {
        return view == null || onClickListener == null;
    }
}
