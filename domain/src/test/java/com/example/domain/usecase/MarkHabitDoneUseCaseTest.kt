package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class MarkHabitDoneUseCaseTest {

    private val mockRepository = Mockito.mock(HabitRepository::class.java)

    private val markHabitDoneUseCase: MarkHabitDoneUseCase = MarkHabitDoneUseCase(mockRepository)

    @Test
    fun markDone_unfulfilledHabit_ExtendedDoneDates() = runBlocking {
        val habit = Habit()
        markHabitDoneUseCase.markDone(habit)
        assertEquals(1, habit.doneDates.size)
    }
}