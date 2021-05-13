package com.example.habittracker

import android.app.Application
import com.example.habittracker.database.HabitDao
import com.example.habittracker.database.HabitDatabase
import com.example.habittracker.database.RequestDao
import com.example.habittracker.network.HabitApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class HabitTrackerApplication : Application() {
    lateinit var database: HabitDatabase
        private set
    lateinit var habitDao: HabitDao
    lateinit var requestDao: RequestDao
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = HabitDatabase.getDatabase(this)
        val webservice: HabitApiService by lazy {
            retrofit.create(HabitApiService::class.java)
        }
        habitDao = database.habitDao()
        requestDao = database.requestDao()
        repository = HabitRepository(habitDao, requestDao, webservice, client)
    }

    private val requestManager: RequestManager by lazy {
        RequestManager(
            requestDao,
            applicationContext
        )
    }

    private val client: OkHttpClient by lazy {
        val authorizationInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
                .header(
                    "Authorization",
                    "6395dfd0-b640-487a-99b7-1cfc567ca457"
                )
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }

        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(requestManager.interceptor)
            .dispatcher(dispatcher)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    companion object {
        private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"

        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }
}
