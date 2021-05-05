package com.example.habittracker

import android.app.Application
import androidx.room.Room
import com.example.habittracker.model.CardDatabase
import com.example.habittracker.model.CardRepository


class HabitTrackerApplication : Application() {
    lateinit var database: CardDatabase
        private set
    lateinit var repository: CardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room
            .databaseBuilder(this, CardDatabase::class.java, "card_database")
            .allowMainThreadQueries()
            .build()
        repository = CardRepository(database.cardDao())
    }
}
