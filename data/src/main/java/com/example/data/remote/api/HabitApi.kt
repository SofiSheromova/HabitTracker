package com.example.data.remote.api

import com.example.data.model.HabitDone
import com.example.data.model.HabitEntity
import com.example.data.model.HabitUid
import retrofit2.http.*

interface HabitApi {
    @GET("habit")
    suspend fun getHabits(): List<HabitEntity>

    @PUT("habit")
    suspend fun updateHabit(@Body json: HabitEntity): HabitUid

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body habitUid: HabitUid)

    @POST("habit_done")
    suspend fun markHabitDone(@Body habitDone: HabitDone)
}
