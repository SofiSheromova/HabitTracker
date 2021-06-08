package com.example.data.model

import com.squareup.moshi.Json

class Error(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
)