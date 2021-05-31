package com.example.domain.repository

import com.example.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    val allHabits: Flow<List<Habit>>

    suspend fun start()

    suspend fun refresh(): Boolean

    suspend fun insert(habit: Habit)

    suspend fun update(habit: Habit)

    suspend fun delete(habit: Habit)

    suspend fun markDone(habit: Habit, time: Long)
}