package com.wonderelf.timer.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Author: cl
 * Time: 2018/11/9
 * Description:
 */
class MeatBean() : BaseInfo(), Parcelable {

    var name = ""
    var img = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        img = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(img)
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