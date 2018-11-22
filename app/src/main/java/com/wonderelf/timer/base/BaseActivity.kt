package com.wonderelf.timer.base

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.remair.util.AppManager
import com.remair.util.LogUtils
import com.remair.util.StatusBarUtil
import com.wonderelf.timer.R


/**
 * Author: cl
 * Time: 2018/11/8
 * Description:
 */
abstract class BaseActivity : AppCompatActivity() {

    protected abstract fun initUI()
    protected abstract fun initData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.getAppManager().addActivity(this)
        initUI()
        initData()
    }

    open fun initStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat
                .getColor(this, R.color.color_D9D9D9), 0)
    }

    open fun initStatusBar(color: Int) {
        StatusBarUtil.setColor(this, ContextCompat
                .getColor(this, color), 0)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)

            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }

    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager().finishActivity(this)
    }

}


