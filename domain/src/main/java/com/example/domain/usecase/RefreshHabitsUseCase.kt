package com.example.domain.usecase

import com.example.domain.repository.HabitRepository

class RefreshHabitsUseCase(private val repository: HabitRepository) {
    suspend fun refresh() {
        repository.refresh()
    }
}