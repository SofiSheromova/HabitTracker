package com.example.domain.usecase

import com.example.domain.repository.HabitRepository

class GetAllHabitsUseCase(private val habitRepository: HabitRepository) {
//    fun getAll(): Flow<List<Habit>> = habitRepository.allHabits
}