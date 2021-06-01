package com.example.habittracker

import android.app.Application
import com.example.habittracker.di.component.AppComponent
import com.example.habittracker.di.component.DaggerAppComponent


class HabitTrackerApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .setApplication(this)
            .build()
    }
}
