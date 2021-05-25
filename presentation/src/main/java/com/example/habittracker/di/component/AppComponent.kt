package com.example.habittracker.di.component

import com.example.habittracker.di.module.AppModule
import com.example.habittracker.ui.info.InfoFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(infoFragment: InfoFragment)
}