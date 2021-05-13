package com.example.habittracker

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.habittracker.database.HabitDatabase
import com.example.habittracker.network.HabitApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException


class HabitTrackerApplication : Application() {
    lateinit var database: HabitDatabase
        private set
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = HabitDatabase.getDatabase(this)
        val habitApi: HabitApiService by lazy {
            retrofit.create(HabitApiService::class.java)
        }
        repository = HabitRepository(
            database.habitDao(),
            database.requestDao(),
            habitApi,
            ::newCall
        )
    }

    private fun isConnected(): Boolean {
        val connectivityManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private val requestManager: RequestManager by lazy {
        RequestManager(database.requestDao(), ::isConnected)
    }

    private val client: OkHttpClient by lazy {
        val authorizationInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
                .header(
                    "Authorization",
                    AUTHORIZATION_TOKEN
                )
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("TAG-NETWORK", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestManager.interceptor)
            .dispatcher(dispatcher)
            .build()
    }

    private fun newCall(request: Request) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG-NETWORK", "newCall() Failure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("TAG-NETWORK", "newCall() Success: ${response.code}")
            }
        })
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
        private const val AUTHORIZATION_TOKEN = "6395dfd0-b640-487a-99b7-1cfc567ca457"

        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }
}
