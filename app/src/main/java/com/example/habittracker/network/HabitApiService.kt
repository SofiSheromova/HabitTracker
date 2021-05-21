package com.example.habittracker.network

import retrofit2.http.*

interface HabitApiService {
    @GET("habit")
    suspend fun getHabits(): List<HabitJson>

    @PUT("habit")
    suspend fun updateHabit(@Body json: HabitJson): HabitUid

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body habitUid: HabitUid)

    @POST("habit_done")
    suspend fun habitDone(@Body habitDone: HabitDone)
}
