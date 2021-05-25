package com.example.data.di.module

import android.content.Context
import android.util.Log
import com.example.data.Constants
import com.example.data.HabitRepositoryImpl
import com.example.data.di.interfaces.DatabaseInfo
import com.example.data.di.interfaces.StorageRequestsOkHttpClient
import com.example.data.local.db.HabitDatabase
import com.example.data.remote.api.HabitApi
import com.example.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import okhttp3.*
import java.io.IOException
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
    fun provideHabitDatabase(@DatabaseInfo dbName: String, context: Context): HabitDatabase {
        return HabitDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitApi: HabitApi,
        @StorageRequestsOkHttpClient client: OkHttpClient,
        habitDatabase: HabitDatabase
    ): HabitRepository {

        fun newCall(request: Request) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("TAG-NETWORK", "Failure: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {}
            })
        }

        return HabitRepositoryImpl(
            habitDatabase.habitDao(),
            habitDatabase.requestDao(),
            habitApi,
            ::newCall
        )
    }
}