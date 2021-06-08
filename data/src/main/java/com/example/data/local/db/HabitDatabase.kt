package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.Constants
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.HabitEntity
import com.example.data.model.RequestEntity

@Database(
    entities = [HabitEntity::class, RequestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun requestDao(): RequestDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}