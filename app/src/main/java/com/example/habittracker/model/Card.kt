package com.example.habittracker.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp
import kotlin.random.Random


fun generateColor(): String {
    return String.format(
        "#%06X", 0xFFFFFF and Color.argb(
            100,
            Random.nextInt(180, 240),
            Random.nextInt(180, 240),
            Random.nextInt(180, 240)
        )
    )
}

class Card(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Int,
    var color: String? = generateColor()
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

        private val _cards = mutableMapOf<String, Card>()

        fun getAll(): List<Card> {
            return _cards.values.toList()
        }

        fun getById(id: String): Card? {
            return _cards[id]
        }

        fun insertAll(vararg cards: Card) {
            for (card in cards) {
                _cards[card.id] = card
            }
        }

        fun update(id: String, card: Card): Boolean {
            val originalCard = _cards[id] ?: return false
            originalCard.title = card.title
            originalCard.description = card.description
            originalCard.periodicity = card.periodicity
            originalCard.type = card.type
            originalCard.priority = card.priority
            //originalCard.color = card.color
            return true
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

