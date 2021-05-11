package com.example.habittracker

import android.app.Application
import com.example.habittracker.model.HabitDatabase
import com.example.habittracker.network.HabitApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class HabitTrackerApplication : Application() {
    lateinit var database: HabitDatabase
        private set
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = HabitDatabase.getDatabase(this)
        val webservice: HabitApiService by lazy {
            retrofit.create(HabitApiService::class.java)
        }
        repository = HabitRepository(database.habitDao(), webservice)
    }

    companion object {
        private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"

        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build()
        }
    }
}
