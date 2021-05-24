package com.example.data.remote.api

import com.example.data.model.HabitDone
import com.example.data.model.HabitJson
import com.example.data.model.HabitUid
import retrofit2.http.*

interface HabitApi {
    @GET("habit")
    suspend fun getHabits(): List<HabitJson>

    @PUT("habit")
    suspend fun updateHabit(@Body json: HabitJson): HabitUid

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body habitUid: HabitUid)

    @POST("habit_done")
    suspend fun habitDone(@Body habitDone: HabitDone)
}
