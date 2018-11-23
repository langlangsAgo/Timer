package com.wonderelf.timer.util

import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.countdowntime.CountDownTimerSupport

/**
 * Author: cl
 * Time: 2018/11/23
 * Description:
 */
class CountDownTimeUtils {

    companion object {
        var timerMap = mutableMapOf<Int, CountDownTimerSupport>()
        var allList = mutableListOf<TypeDetailBean>() //数据源
    }

    fun addTimeList(bean: TypeDetailBean) {
        allList.add(0, bean)
    }

    fun deleteBean(bean: TypeDetailBean) {
        allList.remove(bean)
    }

    fun getItemBean(position: Int): TypeDetailBean {
        return allList[position]
    }
}