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
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }
}