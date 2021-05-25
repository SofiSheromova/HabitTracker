package com.example.data.di.module

import android.util.Log
import com.example.data.di.interfaces.SimpleOkHttpClient
import com.example.data.local.db.HabitDatabase
import com.example.data.remote.RequestManager
import com.example.data.remote.api.HabitApi
import com.example.data.remote.builder.RetrofitBuilder
import com.example.data.remote.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
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
        headerInterceptor: HeaderInterceptor
    ): Retrofit = retrofitBuilder
        .addInterceptors(headerInterceptor)
        .build()

    @Singleton
    @Provides
    fun provideHabitsApi(retrofit: Retrofit): HabitApi = retrofit.create(HabitApi::class.java)

    @SimpleOkHttpClient
    @Singleton
    @Provides
    fun provideSimpleClient(
        headerInterceptor: HeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .dispatcher(dispatcher)
            .build()
    }

    @Provides
    fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor()
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message -> Log.d("TAG-NETWORK", message) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    fun provideRequestManager(
        habitDatabase: HabitDatabase,
        client: OkHttpClient
    ): RequestManager {
        // TODO тут пока заглушка
        return RequestManager(habitDatabase.requestDao(), { true }, client)
    }
}