package com.example.habittracker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.habittracker.model.*
import com.example.habittracker.network.HabitApiService
import com.example.habittracker.network.HabitProperty
import java.util.*

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitService: HabitApiService
) {
    private val mediator: MediatorLiveData<List<Habit>> = MediatorLiveData<List<Habit>>()

    init {
        mediator.addSource(habitDao.getAll()) { result ->
            result?.let {
                mediator.value = it.map { habitRoomModel -> habitRoomModel.toHabit() }
            }
        }
    }

    val allHabits: LiveData<List<Habit>> = liveData {
        emitSource(mediator)
        try {
            val habits = habitService.getHabits()
            for (habit in habits) {
                Log.d("TAG-NETWORK", "Success $habit")
            }
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }
//        habitDao.createHabit(*habits.toHabitRoomModel())
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

    private fun HabitProperty.toHabit(): Habit {
        return Habit(
            this.title,
            this.description,
            Periodicity(this.count, this.frequency),
            Type.valueOf(this.type),
            Priority.valueOf(this.priority),
            this.color,
            this.uid,
            Date(this.date)
        )
    }
}