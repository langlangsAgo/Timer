package com.wonderelf.timer.util;


import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.remair.util.LogUtils;
import com.wonderelf.timer.R;
import com.wonderelf.timer.activity.AllDetailActivity;
import com.wonderelf.timer.base.XApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.N;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Author: cl
 * Time: 2018/11/20
 * Description:
 */
public class NotificationUtil extends ContextWrapper {

    public static final String id = "channel_default";
    public static final String name = XApplication.Companion.instance().getResources().getText(R.string.end_time_send).toString();
    private Context mContext;
    private int requestCode = (int) SystemClock.uptimeMillis();
    private static Notification notification;
    private NotificationManager manager;
    private static NotificationCompat.Builder cBuilder;

    public NotificationUtil(Context context) {
        super(context);
        mContext = context;
        cBuilder = new NotificationCompat.Builder(mContext, id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
//        channel.enableVibration(true);
//        channel.setVibrationPattern(new long[]{1000, 500, 2000});
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void sendNotification(String title, String content, int notification_ID) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            notifyCustomView(title, content, notification_ID);
        } else {
            notifyCustomView(title, content, notification_ID);
        }
    }

    public Notification getNotification() {
        return this.notification;
    }

    private void notifyCustomView(String title, String content, int notification_ID) {
        //设置想要展示的数据内容
        Intent intent = new Intent(mContext, AllDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 自定义布局
//        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
//                R.layout.item_notification);
//        remoteViews.setTextViewText(R.id.tv_notify_name, name + getResources().getText(R.string.end_time_tip));

        // 设置通知属性
        setCompatBuilder(pIntent, title, content);
        notification = cBuilder.build();
        // 添加进notification
        // 发送该通知
        getManager().notify(notification_ID, notification);
    }

    /**
     * 设置在顶部通知栏中的各种信息
     *
     * @param pendingIntent
     * @param title
     * @param content
     */
    private void setCompatBuilder(PendingIntent pendingIntent, String title, String content) {
//        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
//        // 将Intent封装进PendingIntent中，点击通知的消息后，就会启动对应的程序
//        PendingIntent pIntent = PendingIntent.getActivity(mContext,
//                requestCode, intent, FLAG);

        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(R.drawable.icon_pop_clock);// 设置顶部状态栏的小图标
        cBuilder.setWhen(System.currentTimeMillis());
        cBuilder.setAutoCancel(true); //点击后自动消失
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        cBuilder.setTicker(getResources().getText(R.string.new_notification).toString());// 在顶部状态栏中的提示信息
        cBuilder.setContentTitle(title);// 设置通知中心的标题
        cBuilder.setContentText(content + getResources().getText(R.string.end_time_tip));// 设置通知中心中的内容
//        cBuilder.setContent(remoteViews);
        cBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_pop_clock_big));
        cBuilder.setVisibility(VISIBILITY_PUBLIC); // 设置显示在锁屏上
//        if (Build.VERSION.SDK_INT >= 26) {
//
//        }else {
//
//        }
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
         * Notification.DEFAULT_SOUND：系统默认铃声。
         * Notification.DEFAULT_VIBRATE：系统默认震动。
         * Notification.DEFAULT_LIGHTS：系统默认闪光。
         * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
         */
        cBuilder.setDefaults(Notification.DEFAULT_VIBRATE); // 设置震动
//        cBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)); //设置系统闹钟铃声
    }

    private static Ringtone mRingTone;
    /**
     * 播放闹钟铃声
     *
     * @param mContext
     */
    public static void playRingtone(Context mContext) {
        stopRingtone(); // 前一个如果正在播放就暂停
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mRingTone = RingtoneManager.getRingtone(mContext, notification);
        if (mRingTone != null && !mRingTone.isPlaying()) {
            mRingTone.play();
        }
    }

    /**
     * 停止播放
     */
    public static void stopRingtone() {
        if (mRingTone != null && mRingTone.isPlaying()) {
            mRingTone.stop();
        }
    }

    /**
     * 判断通知权开关是否打开
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {
        boolean isEnabled = false;
        if (Build.VERSION.SDK_INT >= N) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            isEnabled = notificationManagerCompat.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= KITKAT) {
            String CHECK_OP_NO_THROW = "checkOpNoThrow";
            String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null;
            /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                        String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                isEnabled = ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isEnabled;
    }

}
