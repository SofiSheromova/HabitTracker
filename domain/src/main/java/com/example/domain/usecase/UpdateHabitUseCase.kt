package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository

class UpdateHabitUseCase(private val repository: HabitRepository) {
    suspend fun update(original: Habit, newState: Habit) {
        repository.update(original, newState)
    }
}