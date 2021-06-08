package com.example.habittracker.di.module

import androidx.lifecycle.ViewModel
import com.example.domain.usecase.*
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.model.HabitFulfillmentReportFormatter
import com.example.habittracker.ui.cards.CardsViewModel
import com.example.habittracker.ui.editor.EditorViewModel
import com.example.habittracker.ui.home.DisplayOptionsViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FactoryModule {
    @Singleton
    @Provides
    fun provideCardsViewModelFactory(
        getAllHabitsUseCase: GetAllHabitsUseCase,
        refreshHabitsUseCase: RefreshHabitsUseCase,
        markHabitDoneUseCase: MarkHabitDoneUseCase,
        reportUseCase: HabitFulfillmentReportUseCase,
        reportFormatter: HabitFulfillmentReportFormatter,
    ): CardsViewModel.Factory {
        return object : CardsViewModel.Factory {
            override var displayOptions: DisplayOptions = DisplayOptions()

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CardsViewModel(
                        displayOptions,
                        getAllHabitsUseCase,
                        refreshHabitsUseCase,
                        markHabitDoneUseCase,
                        reportUseCase,
                        reportFormatter
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
    ): EditorViewModel.Factory {
        return object : EditorViewModel.Factory {
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
    fun provideDisplayOptionsViewModelFactory(): DisplayOptionsViewModel.Factory {
        return object : DisplayOptionsViewModel.Factory {
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