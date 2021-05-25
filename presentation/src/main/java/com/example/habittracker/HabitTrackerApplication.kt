package com.example.habittracker

import android.app.Application
import com.example.habittracker.di.component.AppComponent
import com.example.habittracker.di.component.DaggerAppComponent
import com.example.habittracker.di.module.AppModule


class HabitTrackerApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
