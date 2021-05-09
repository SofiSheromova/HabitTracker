package com.example.habittracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.habittracker.model.Habit
import com.example.habittracker.model.HabitDao
import com.example.habittracker.model.HabitRoomModel

class HabitRepository(private val habitDao: HabitDao) {
    private val mediator: MediatorLiveData<List<Habit>> = MediatorLiveData<List<Habit>>()
    val allHabits: LiveData<List<Habit>> = mediator

    init {
        mediator.addSource(habitDao.getAll()) { result ->
            result?.let { mediator.value = it.map { habitRoomModel -> habitRoomModel.toHabit() } }
        }
    }

    suspend fun insert(habit: Habit) {
        habitDao.insertAll(habit.toRoomModel())
    }

    suspend fun update(original: Habit, newState: Habit) {
        original.update(newState)
        habitDao.updateAll(original.toRoomModel())
    }

    suspend fun delete(habit: Habit) {
        habitDao.delete(habit.toRoomModel())
    }

    private fun Habit.toRoomModel(): HabitRoomModel {
        return HabitRoomModel(
            this.title,
            this.description,
            this.periodicity,
            this.type,
            this.priority,
            this.color,
            this.uid,
            this.date
        )
    }

    private fun HabitRoomModel.toHabit(): Habit {
        return Habit(
            this.title,
            this.description,
            this.periodicity,
            this.type,
            this.priority,
            this.color,
            this.uid,
            this.date
        )
    }
}