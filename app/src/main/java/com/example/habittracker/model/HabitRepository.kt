package com.example.habittracker.model

import androidx.lifecycle.LiveData

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: LiveData<List<Habit>> = habitDao.getAll()

    suspend fun insertAll(vararg habits: Habit) {
        habitDao.insertAll(*habits)
    }

    suspend fun update(original: Habit, newState: Habit) {
        original.update(newState)
        habitDao.updateAll(original)
    }

    suspend fun delete(habit: Habit) {
        habitDao.delete(habit)
    }
}