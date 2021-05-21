package com.example.habittracker.network

import com.squareup.moshi.Json

class HabitUid(
    @Json(name = "uid") val uid: String,
)