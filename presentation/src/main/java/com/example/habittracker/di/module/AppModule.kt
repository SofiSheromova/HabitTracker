package com.example.habittracker.di.module

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.example.data.di.module.NetworkModule
import com.example.data.di.module.RepositoryModule
import com.example.habittracker.R
import com.example.habittracker.di.interfaces.DefaultCardColor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, RepositoryModule::class, FactoryModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideRecourses(application: Application): Resources {
        return application.resources
    }

    @DefaultCardColor
    @Singleton
    @Provides
    fun provideDefaultCardColor(resources: Resources): Int {
        return resources.getColor(R.color.defaultCardColor, null)
    }
}
