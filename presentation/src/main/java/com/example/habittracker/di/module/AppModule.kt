package com.example.habittracker.di.module

import android.app.Application
import android.content.Context
import com.example.data.di.module.NetworkModule
import com.example.data.di.module.RepositoryModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, RepositoryModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    // TODO: это тестовое, потом убрать
    @Singleton
    @Provides
    fun provideAppName(application: Application): String {
        return application.applicationContext.javaClass.simpleName
    }
}