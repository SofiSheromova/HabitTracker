package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class RefreshHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend fun refresh(): Boolean = repository.refresh()
}