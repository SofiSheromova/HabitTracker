package com.example.data

import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.HabitEntityMapper
import com.example.data.model.RequestEntity
import com.example.data.remote.api.HabitApi
import com.example.domain.repository.HabitRepository
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.IOException


class HabitRepositoryImplTest {
    private val mockHabitDao: HabitDao = mock(HabitDao::class.java)
    private val mockRequestDao: RequestDao = mock(RequestDao::class.java)
    private val mockHabitApi: HabitApi = mock(HabitApi::class.java)
    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        whenever(mockRequestDao.getAll()).thenReturn(flow { emit(listOf<RequestEntity>()) })
        repository = HabitRepositoryImpl(
            mockHabitDao,
            mockRequestDao,
            mockHabitApi,
            HabitEntityMapper(),
        )
    }

    @Test
    fun successRefresh(): Unit = runBlocking {
        given(mockHabitApi.getHabits()).willReturn(listOf())
        val actual = repository.refresh()

        assertTrue(actual)
        verify(mockHabitApi, times(1)).getHabits()
    }

    @Test
    fun failedRefresh(): Unit = runBlocking {
        given(mockHabitApi.getHabits()).will { throw  IOException() }
        val actual = repository.refresh()

        assertFalse(actual)
        verify(mockHabitApi, times(1)).getHabits()
    }
}