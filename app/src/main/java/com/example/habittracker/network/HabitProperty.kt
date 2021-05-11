package com.example.habittracker.network

import android.util.Log
import com.squareup.moshi.Json
import java.util.*

class HabitProperty(
    @Json(name = "uid") val uid: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String = "",
    @Json(name = "priority") val priority: Int = 0,
    @Json(name = "type") val type: Int = 0,
    @Json(name = "count") val count: Int = 1,
    @Json(name = "frequency") val frequency: Int = 1,
    @Json(name = "color") val color: Int = -1,
    @Json(name = "date") val date: Long = Date().time,
    @Json(name = "done_dates") val doneDates: List<Long> = listOf()
) {
    init {
        Log.d("TAG-NETWORK", uid)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(HabitProperty::class.java.getName()).append('@')
            .append(Integer.toHexString(System.identityHashCode(this))).append('[')
        sb.append("uid")
        sb.append('=')
        sb.append(uid ?: "<null>")
        sb.append(',')
        sb.append("title")
        sb.append('=')
        sb.append(title ?: "<null>")
        sb.append(',')
        sb.append("description")
        sb.append('=')
        sb.append(description ?: "<null>")
        sb.append(',')
        sb.append("priority")
        sb.append('=')
        sb.append(priority ?: "<null>")
        sb.append(',')
        sb.append("type")
        sb.append('=')
        sb.append(type ?: "<null>")
        sb.append(',')
        sb.append("count")
        sb.append('=')
        sb.append(count ?: "<null>")
        sb.append(',')
        sb.append("frequency")
        sb.append('=')
        sb.append(frequency ?: "<null>")
        sb.append(',')
        sb.append("color")
        sb.append('=')
        sb.append(color ?: "<null>")
        sb.append(',')
        sb.append("date")
        sb.append('=')
        sb.append(date ?: "<null>")
        sb.append(',')
        sb.append("doneDates")
        sb.append('=')
        sb.append(doneDates ?: "<null>")
        sb.append(',')
        if (sb[sb.length - 1] == ',') {
            sb.setCharAt(sb.length - 1, ']')
        } else {
            sb.append(']')
        }
        return sb.toString()
    }
}