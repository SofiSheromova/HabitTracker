package com.example.domain.repository

import com.example.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    val allHabits: Flow<List<Habit>>

    suspend fun refresh(): Boolean

    suspend fun insert(habit: Habit)

    suspend fun update(original: Habit, newState: Habit)

    suspend fun delete(habit: Habit)

    suspend fun markDone(habit: Habit)
}