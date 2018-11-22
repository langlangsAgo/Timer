package com.wonderelf.timer.bean

import android.os.Parcel
import android.os.Parcelable
import com.wonderelf.timer.countdowntime.PaintState
import com.wonderelf.timer.countdowntime.TimerState
import java.io.Serializable

/**
 * Author: cl
 * Time: 2018/11/9
 * Description:
 */
class TypeDetailBean : BaseInfo(), Serializable {

    var name = "" //名称
    var img = "" // 图片
    var isSelect = 0 // 是否选中
    var isDefault = 1 //默认数据
    var totalTime = 0L // 总时间
    var remainingTime = 0L // 剩余时间
    var state = TimerState.DEFAULT // 倒计时默认状态
    var positionId = 0
    override fun toString(): String {
        return "TypeDetailBean(name='$name', img='$img', isSelect=$isSelect, isDefault=$isDefault, totalTime=$totalTime, remainingTime=$remainingTime, state=$state, positionId=$positionId)"
    }
}