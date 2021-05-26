package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class MarkHabitDoneUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend fun markDone(habit: Habit) = repository.markDone(habit)
}