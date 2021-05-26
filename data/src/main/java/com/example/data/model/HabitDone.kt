package com.example.data.model

import com.squareup.moshi.Json
import java.util.*

class HabitDone(
    @Json(name = "habit_uid") val uid: String,
    @Json(name = "date") val date: Long = Date().time,
)