package com.example.habittracker

import android.app.Application
import com.example.habittracker.model.HabitDatabase


class HabitTrackerApplication : Application() {
    lateinit var database: HabitDatabase
        private set
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = HabitDatabase.getDatabase(this)
        repository = HabitRepository(database.habitDao())
    }
}
