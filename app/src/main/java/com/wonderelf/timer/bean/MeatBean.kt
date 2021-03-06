package com.wonderelf.timer.bean

import android.os.Parcel
import android.os.Parcelable
import com.wonderelf.timer.countdowntime.TimerState

/**
 * Author: cl
 * Time: 2018/11/9
 * Description:
 */
class MeatBean() : BaseInfo(), Parcelable {

    var name = ""
    var img = 0
    var bg = 0
    var state = TimerState.DEFAULT
    var totalTime = 0L
    var remainingTime = 0L

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        img = parcel.readInt()
        bg = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(img)
        parcel.writeInt(bg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeatBean> {
        override fun createFromParcel(parcel: Parcel): MeatBean {
            return MeatBean(parcel)
        }

        override fun newArray(size: Int): Array<MeatBean?> {
            return arrayOfNulls(size)
        }
    }

}