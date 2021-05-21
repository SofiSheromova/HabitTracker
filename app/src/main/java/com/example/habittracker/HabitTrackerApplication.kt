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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException


class HabitTrackerApplication : Application() {
    val database: HabitDatabase by lazy {
        HabitDatabase.getDatabase(this)
    }

    val repository: HabitRepository by lazy {
        val habitApi = retrofit.create(HabitApiService::class.java)

        HabitRepository(database.habitDao(), database.requestDao(), habitApi, ::newCall)
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithSavingFailedRequests)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val simpleClient: OkHttpClient by lazy {
        val authorizationInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
                .header(
                    "Authorization",
                    BuildConfig.AUTHORIZATION_TOKEN
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
            .dispatcher(dispatcher)
            .build()
    }

    private val requestManager: RequestManager by lazy {
        RequestManager(database.requestDao(), ::isConnected, simpleClient)
    }

    private val clientWithSavingFailedRequests: OkHttpClient by lazy {
        simpleClient.newBuilder()
            .addInterceptor(requestManager.interceptor)
            .build()
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

    private fun newCall(request: Request) {
        clientWithSavingFailedRequests.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG-NETWORK", "Failure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {}
        })
    }

    suspend fun saveRequests() = withContext(Dispatchers.IO) {
        requestManager.saveState()
        Log.d("TAG-NETWORK", "Requests saved")
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
