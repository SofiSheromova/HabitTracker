package com.example.habittracker.di.module

import com.example.domain.repository.HabitRepository
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.GetAllHabitsUseCase
import com.example.domain.usecase.RefreshHabitsUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import dagger.Module
import dagger.Provides

@Module
class HabitsModule {

    @Provides
    fun provideGetAllHabitsUseCase(habitRepository: HabitRepository): GetAllHabitsUseCase {
        return GetAllHabitsUseCase(habitRepository)
    }

    @Provides
    fun provideDeleteHabitUseCase(habitRepository: HabitRepository): DeleteHabitUseCase {
        return DeleteHabitUseCase(habitRepository)
    }

    @Provides
    fun provideRefreshHabitsUseCase(habitRepository: HabitRepository): RefreshHabitsUseCase {
        return RefreshHabitsUseCase(habitRepository)
    }

    @Provides
    fun provideUpdateHabitUseCase(habitRepository: HabitRepository): UpdateHabitUseCase {
        return UpdateHabitUseCase(habitRepository)
    }
}