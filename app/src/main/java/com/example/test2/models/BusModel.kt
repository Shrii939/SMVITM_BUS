package com.example.test2.models

import android.os.Parcel
import android.os.Parcelable

data class BusModel(
    val name: String,
    val startPoint: String,
    val startTime: String,
    val endPoint: String,
    val endTime: String,
    val stops: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(startPoint)
        parcel.writeString(startTime)
        parcel.writeString(endPoint)
        parcel.writeString(endTime)
        parcel.writeStringList(stops)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusModel> {
        override fun createFromParcel(parcel: Parcel): BusModel {
            return BusModel(parcel)
        }

        override fun newArray(size: Int): Array<BusModel?> {
            return arrayOfNulls(size)
        }
    }
}