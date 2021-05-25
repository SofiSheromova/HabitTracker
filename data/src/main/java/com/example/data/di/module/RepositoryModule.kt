package com.example.data.di.module

import android.content.Context
import androidx.room.Room
import com.example.data.Constants
import com.example.data.HabitRepositoryImpl
import com.example.data.di.interfaces.DatabaseInfo
import com.example.data.local.db.HabitDatabase
import com.example.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return Constants.DATABASE_NAME
    }

    @Provides
    @Singleton
    fun provideHabitDatabase(@DatabaseInfo dbName: String, context: Context): HabitDatabase {
        return Room.databaseBuilder(context, HabitDatabase::class.java, dbName)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(repository: HabitRepositoryImpl): HabitRepository {
        return repository
    }
}