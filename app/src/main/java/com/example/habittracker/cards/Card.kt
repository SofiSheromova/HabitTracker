package com.example.habittracker.cards

import org.json.JSONException
import org.json.JSONObject
import java.sql.Timestamp


class Card(
    var id: String,
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Int,
    var color: String? = null
) {

    constructor(
        title: String,
        description: String,
        periodicity: Periodicity,
        type: Type,
        priority: Int,
        color: String? = null
    ) : this(
        Timestamp(System.currentTimeMillis()).toString(),
        title,
        description,
        periodicity,
        type,
        priority,
        color
    )

    constructor() : this(
        "",
        "",
        Periodicity(1, 1),
        Type.GOOD,
        0,
    )

    companion object {
        private const val ID = "CARD_ID"
        private const val TITLE_KEY = "TITLE"
        private const val DESCRIPTION_KEY = "DESCRIPTION"
        private const val PRIORITY_KEY = "PRIORITY"
        private const val TYPE_KEY = "TYPE"
        private const val COLOR_KEY = "COLOR"
        private const val REPETITIONS_NUMBER_KEY = "REPETITIONS_NUMBER"
        private const val DAYS_NUMBER_KEY = "DAYS_NUMBER"

        private fun fromJson(jsonString: String): Card {
            val json = JSONObject(jsonString)
            return Card(
                json.getString(ID),
                json.getString(TITLE_KEY),
                json.getString(DESCRIPTION_KEY),
                Periodicity(
                    json.getInt(REPETITIONS_NUMBER_KEY),
                    json.getInt(DAYS_NUMBER_KEY)
                ),
                Type.valueOf(json.getInt(TYPE_KEY)),
                json.getInt(PRIORITY_KEY),
                json.getString(COLOR_KEY)
            )
        }

        fun fromJsonOrNull(jsonString: String?): Card? {
            if (jsonString == null)
                return null
            return try {
                fromJson(jsonString)
            } catch (e: JSONException) {
                null
            }
        }
    }

    fun copy(): Card {
        return Card(this.title, this.description, this.periodicity, this.type, this.priority)
    }

    fun toJSON(): JSONObject {
        val content = mutableMapOf(
            ID to id,
            TITLE_KEY to title,
            DESCRIPTION_KEY to description,
            PRIORITY_KEY to priority,
            REPETITIONS_NUMBER_KEY to periodicity.repetitionsNumber,
            DAYS_NUMBER_KEY to periodicity.daysNumber,
            TYPE_KEY to type.value,
            COLOR_KEY to color
        )

        return JSONObject(content as Map<*, *>)
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

