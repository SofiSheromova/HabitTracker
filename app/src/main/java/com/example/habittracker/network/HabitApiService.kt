package com.example.habittracker.network

import retrofit2.http.GET
import retrofit2.http.Headers

interface HabitApiService {
    @Headers("Authorization:6395dfd0-b640-487a-99b7-1cfc567ca457")
    @GET("habit")
    suspend fun getHabits(): List<HabitProperty>
}
