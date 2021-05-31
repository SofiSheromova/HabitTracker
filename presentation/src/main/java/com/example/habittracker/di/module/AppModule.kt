package com.example.habittracker.di.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.data.di.module.NetworkModule
import com.example.data.di.module.RepositoryModule
import com.example.domain.usecase.*
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.ui.cards.CardsViewModel
import com.example.habittracker.ui.cards.CardsViewModelFactory
import com.example.habittracker.ui.editor.EditorViewModel
import com.example.habittracker.ui.editor.EditorViewModelFactory
import com.example.habittracker.ui.home.DisplayOptionsViewModel
import com.example.habittracker.ui.home.DisplayOptionsViewModelFactory
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

    @Singleton
    @Provides
    fun provideEditorViewModelFactory(
        insertHabitUseCase: InsertHabitUseCase,
        updateHabitUseCase: UpdateHabitUseCase,
        deleteHabitUseCase: DeleteHabitUseCase
    ): EditorViewModelFactory {
        return object : EditorViewModelFactory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return EditorViewModel(
                        insertHabitUseCase,
                        updateHabitUseCase,
                        deleteHabitUseCase
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    @Singleton
    @Provides
    fun provideDisplayOptionsViewModelFactory(): DisplayOptionsViewModelFactory {
        return object : DisplayOptionsViewModelFactory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DisplayOptionsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DisplayOptionsViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
