package com.wonderelf.timer.bean

open class BaseInfo {

    var ret: Int = 1
    var msg: String = "ok"

    fun checkStatus(): Boolean {
        return ret == 200
    }
}