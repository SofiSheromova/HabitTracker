package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class RefreshHabitsUseCaseTest {
    private val mockRepository: HabitRepository = mock(HabitRepository::class.java)
    private val refreshHabitsUseCase: RefreshHabitsUseCase = RefreshHabitsUseCase(mockRepository)

    @Before
    fun setUp(): Unit = runBlocking {
        whenever(mockRepository.refresh()).thenReturn(true)
    }

    @Test
    fun check(): Unit = runBlocking {
        refreshHabitsUseCase.refresh()
        verify(mockRepository, times(1)).refresh()
    }
}