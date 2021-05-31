package com.example.habittracker.di.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.data.di.module.NetworkModule
import com.example.data.di.module.RepositoryModule
import com.example.domain.usecase.GetAllHabitsUseCase
import com.example.domain.usecase.LatestDoneDatesHabitUseCase
import com.example.domain.usecase.MarkHabitDoneUseCase
import com.example.domain.usecase.RefreshHabitsUseCase
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.ui.cards.CardsViewModelFactory
import com.example.habittracker.ui.cards.CardsViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, RepositoryModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideCardsViewModelFactory(
        application: Application,
        getAllHabitsUseCase: GetAllHabitsUseCase,
        refreshHabitsUseCase: RefreshHabitsUseCase,
        markHabitDoneUseCase: MarkHabitDoneUseCase,
        latestDoneDatesHabitUseCase: LatestDoneDatesHabitUseCase
    ): CardsViewModelFactory {
        return object : CardsViewModelFactory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CardsViewModel(
                        application as HabitTrackerApplication,
                        getAllHabitsUseCase,
                        refreshHabitsUseCase,
                        markHabitDoneUseCase,
                        latestDoneDatesHabitUseCase,
                        DisplayOptions()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
