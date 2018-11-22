package com.wonderelf.timer.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.annotation.RequiresApi
import com.remair.util.toast.ToastUtils
import com.wonderelf.timer.util.NotificationUtil

/**
 * Author: cl
 * Time: 2018/11/8
 * Description:
 */
class XApplication : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // 8.0注册通知渠道
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(NotificationUtil.id, NotificationUtil.name, NotificationManager.IMPORTANCE_HIGH)
            NotificationUtil(instance()).manager.createNotificationChannel(channel)
        }
//        ToastUtils.init(this)
    }
}