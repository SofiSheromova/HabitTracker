package com.example.data.di.module

import android.util.Log
import com.example.data.remote.api.HabitApi
import com.example.data.remote.builder.RetrofitBuilder
import com.example.data.remote.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        retrofitBuilder: RetrofitBuilder,
        client: OkHttpClient
    ): Retrofit = retrofitBuilder
        .setOkHttpClientBuilder(client.newBuilder())
        .build()

    @Singleton
    @Provides
    fun provideHabitsApi(retrofit: Retrofit): HabitApi = retrofit.create(HabitApi::class.java)

    @Singleton
    @Provides
    fun provideClient(
        headerInterceptor: HeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message -> Log.d("TAG-NETWORK", message) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }
    }
}