package com.example.domain.usecase

import com.example.domain.model.DisplayOptions
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetAllHabitsUseCase(private val repository: HabitRepository) {
    fun getAll(): Flow<List<Habit>> = repository.allHabits

    fun getAll(displayOption: DisplayOptions): Flow<List<Habit>> =
        displayOption.filter(repository.allHabits)
}