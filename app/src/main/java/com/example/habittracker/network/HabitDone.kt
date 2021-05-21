package com.example.habittracker.network

import com.squareup.moshi.Json
import java.util.*

class HabitDone(
    @Json(name = "uid") val uid: String,
    @Json(name = "date") val date: Long = Date().time,
)