package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import java.util.*
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend fun update(original: Habit, newState: Habit) {
        val habit = Habit(
            newState.title,
            newState.description,
            newState.periodicity,
            newState.type,
            newState.priority,
            original.color, // newState.color
            original.uid,
            Date()
        ).apply {
            this.markDone(*newState.doneDates.toTypedArray())
        }

        repository.update(habit)
    }
}