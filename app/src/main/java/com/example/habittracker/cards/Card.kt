package com.example.habittracker.cards

import java.sql.Timestamp

class Card(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Int,
    var color: String? = null
) {
    val id: String = Timestamp(System.currentTimeMillis()).toString()

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
