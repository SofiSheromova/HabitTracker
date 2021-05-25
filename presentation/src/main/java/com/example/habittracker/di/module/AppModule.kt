package com.example.habittracker.di.module

import android.content.Context
import com.example.data.di.module.NetworkModule
import com.example.data.di.module.RepositoryModule
import com.example.habittracker.HabitTrackerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, RepositoryModule::class])
class AppModule(private val application: HabitTrackerApplication) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return application.applicationContext
    }

    // TODO: это тестовое, потом убрать
    @Singleton
    @Provides
    fun provideAppName(): String {
        return application.applicationContext.javaClass.simpleName
    }
}