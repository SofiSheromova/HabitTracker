package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class InsertHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend fun insert(habit: Habit) = repository.insert(habit)
}