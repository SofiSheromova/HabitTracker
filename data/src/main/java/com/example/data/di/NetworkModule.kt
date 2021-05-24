package com.example.data.di

import com.example.data.interceptor.HeaderInterceptor
import com.example.data.remote.api.HabitApi
import com.example.data.remote.api.builder.RetrofitBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(
        retrofitBuilder: RetrofitBuilder,
        headerInterceptor: HeaderInterceptor
    ): Retrofit = retrofitBuilder
        .addInterceptors(headerInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideHabitsApi(retrofit: Retrofit): HabitApi = retrofit.create(HabitApi::class.java)

}