package com.example.data

import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.HabitEntityMapper
import com.example.data.model.RequestEntityMapper
import com.example.data.remote.api.HabitApi
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.IOException


class HabitRepositoryImplTest {
    private val mockHabitDao: HabitDao = mock(HabitDao::class.java)
    private val mockRequestDao: RequestDao = mock(RequestDao::class.java)
    private val mockHabitApi: HabitApi = mock(HabitApi::class.java)
    private val newCall: (Request) -> Unit = {}
    private val habitEntityMapper: HabitEntityMapper = HabitEntityMapper()
    private val requestEntityMapper: RequestEntityMapper = RequestEntityMapper()
    private val repository = HabitRepositoryImpl(
        mockHabitDao,
        mockRequestDao,
        mockHabitApi,
        newCall,
        habitEntityMapper,
        requestEntityMapper
    )

    @Test
    fun successRefresh(): Unit = runBlocking {
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