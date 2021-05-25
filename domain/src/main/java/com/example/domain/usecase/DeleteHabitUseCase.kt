package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository

class DeleteHabitUseCase(private val repository: HabitRepository) {
    suspend fun delete(habit: Habit) {
        repository.delete(habit)
    }
}