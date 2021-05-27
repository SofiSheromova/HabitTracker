package com.example.habittracker

import android.app.Application
import com.example.habittracker.di.component.AppComponent
import com.example.habittracker.di.component.DaggerAppComponent
import com.example.habittracker.di.component.ViewModelSubComponent


class HabitTrackerApplication : Application() {
    lateinit var appComponent: AppComponent
    lateinit var viewModelSubComponent: ViewModelSubComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .setApplication(this)
            .build()

        viewModelSubComponent = appComponent
            .viewModelSubComponentBuilder()
            .build()
    }
}
