package com.example.data.model

import com.squareup.moshi.Json

data class HabitUid(
    @Json(name = "uid") val uid: String,
)