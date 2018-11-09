package com.wonderelf.timer.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

/**
 * Author: cl
 * Time: 2018/11/8
 * Description:
 */
class XApplication : Application() {

    private val TAG = "horse"

    companion object {
        var instace: Application? = null
        fun instace() = instace!!
    }

    override fun onCreate() {
        super.onCreate()
        instace = this

    }

    private fun isPackageInstalled(name: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(name, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    fun getPreference(): SharedPreferences {
        return getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }
}