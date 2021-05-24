package com.example.data.model

import com.squareup.moshi.Json
import java.util.*

class HabitJson(
    @Json(name = "uid") var uid: String,
    @Json(name = "title") var title: String,
    @Json(name = "description") var description: String = "",
    @Json(name = "priority") var priority: Int = 0,
    @Json(name = "type") var type: Int = 0,
    @Json(name = "count") var count: Int = 1,
    @Json(name = "frequency") var frequency: Int = 1,
    @Json(name = "color") var color: Int = -1,
    @Json(name = "date") var date: Long = Date().time,
    @Json(name = "done_dates") var doneDates: List<Long> = listOf()
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(HabitJson::class.java.getName()).append('@')
            .append(Integer.toHexString(System.identityHashCode(this))).append('[')
        sb.append("uid")
        sb.append('=')
        sb.append(uid)
        sb.append(',')
        sb.append("title")
        sb.append('=')
        sb.append(title)
        sb.append(',')
        sb.append("description")
        sb.append('=')
        sb.append(description)
        sb.append(',')
        sb.append("priority")
        sb.append('=')
        sb.append(priority)
        sb.append(',')
        sb.append("type")
        sb.append('=')
        sb.append(type)
        sb.append(',')
        sb.append("count")
        sb.append('=')
        sb.append(count)
        sb.append(',')
        sb.append("frequency")
        sb.append('=')
        sb.append(frequency)
        sb.append(',')
        sb.append("color")
        sb.append('=')
        sb.append(color)
        sb.append(',')
        sb.append("date")
        sb.append('=')
        sb.append(date)
        sb.append(',')
        sb.append("doneDates")
        sb.append('=')
        sb.append(doneDates)
        sb.append(',')
        if (sb[sb.length - 1] == ',') {
            sb.setCharAt(sb.length - 1, ']')
        } else {
            sb.append(']')
        }
        return sb.toString()
    }
}