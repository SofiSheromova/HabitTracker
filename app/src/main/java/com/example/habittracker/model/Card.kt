package com.example.habittracker.model

import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp

class Card(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Int,
    var color: String? = null
) : Parcelable {
    val id: String = Timestamp(System.currentTimeMillis()).toString()

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Periodicity(parcel.readInt(), parcel.readInt()),
        Type.valueOf(parcel.readInt()),
        parcel.readInt(),
        parcel.readString()
    )

    constructor() : this(
        "",
        "",
        Periodicity(1, 1),
        Type.GOOD,
        0,
    )

    fun copy(): Card {
        return Card(this.title, this.description, this.periodicity, this.type, this.priority)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(priority)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}

enum class Type(val value: Int) {
    GOOD(0),
    BAD(1);

    companion object {
        fun valueOf(value: Int): Type = values().find { it.value == value } ?: GOOD
    }
}

class Periodicity(_repetitionsNumber: Int, _daysNumber: Int) {
    val repetitionsNumber: Int
    val daysNumber: Int

    init {
        val gcd = getGCD(_repetitionsNumber, _daysNumber)
        repetitionsNumber = _repetitionsNumber / gcd
        daysNumber = _daysNumber / gcd
    }

    private fun getGCD(first: Int, second: Int): Int {
        var n1 = first
        var n2 = second
        while (n1 != n2) {
            if (n1 > n2)
                n1 -= n2
            else
                n2 -= n1
        }
        return n1
    }

    // TODO: адекватно будет работать с ресурсами, но они доступны только внурти Activity
    override fun toString(): String {
        if (repetitionsNumber == 1 && daysNumber == 1) {
            return "Everyday"
        }
        return "$repetitionsNumber times in $daysNumber days"
    }
}

