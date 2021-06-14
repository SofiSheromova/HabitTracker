package com.example.data.di.module

import android.content.Context
import com.example.data.Constants
import com.example.data.HabitRepositoryImpl
import com.example.data.di.interfaces.DatabaseInfo
import com.example.data.local.db.HabitDatabase
import com.example.data.model.HabitEntityMapper
import com.example.data.remote.api.HabitApi
import com.example.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
class RepositoryModule {
    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return Constants.DATABASE_NAME
    }

    @Provides
    @Singleton
    fun provideHabitDatabase(context: Context): HabitDatabase {
        return HabitDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitApi: HabitApi,
        habitDatabase: HabitDatabase,
        habitEntityMapper: HabitEntityMapper,
    ): HabitRepository {
        return HabitRepositoryImpl(
            habitDatabase.habitDao(),
            habitDatabase.requestDao(),
            habitApi,
            habitEntityMapper
        )
    }
}