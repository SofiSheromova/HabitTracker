package com.example.data.remote.builder

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitBuilder {
    private var okHttpClientBuilder: OkHttpClient.Builder? = null
    private var interceptors = mutableListOf<Interceptor>()

    //    private var logEnable: Boolean = BuildConfig.DEBUG
    private var logEnable: Boolean = true
    private var baseUrl = "https://droid-test-server.doubletapp.ru/api/"
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun setOkHttpClientBuilder(okHttpClientBuilder: OkHttpClient.Builder): RetrofitBuilder {
        this.okHttpClientBuilder = okHttpClientBuilder
        return this
    }

    fun addInterceptors(vararg interceptor: Interceptor): RetrofitBuilder {
        interceptors.addAll(interceptor)
        return this
    }

    fun loggingEnable(enable: Boolean): RetrofitBuilder {
        this.logEnable = enable
        return this
    }

    fun setBaseURL(baseUrl: String): RetrofitBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun build(): Retrofit {
        val clientBuilder = (okHttpClientBuilder ?: OkHttpClient.Builder()).apply {
            if (logEnable) {
                addInterceptor(
                    HttpLoggingInterceptor { message -> Log.d("TAG-NETWORK", message) }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }

            interceptors.forEach { addInterceptor(it) }
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}