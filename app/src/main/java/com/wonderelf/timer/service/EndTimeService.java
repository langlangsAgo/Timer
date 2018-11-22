package com.wonderelf.timer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.remair.util.AppManager;
import com.remair.util.LogUtils;
import com.wonderelf.timer.view.PopupEndTime;

/**
 * Author: cl
 * Time: 2018/11/20
 * Description: 计时结束全局弹出对话框
 */
public class EndTimeService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String content = intent.getStringExtra("content");
        PopupEndTime pop = new PopupEndTime(AppManager.getAppManager().currentActivity(), content);
        pop.showPopupWindow();
        stopService(intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
