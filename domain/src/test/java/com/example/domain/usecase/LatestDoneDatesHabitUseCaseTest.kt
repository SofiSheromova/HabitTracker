package com.example.domain.usecase

import com.example.domain.model.Habit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LatestDoneDatesHabitUseCaseTest {
    @Test
    fun getDoneDatesForLastDays_habitWithoutDoneDates_returnEmptyList() {
        val actual = LatestDoneDatesHabitUseCase()
            .getDoneDatesForLastDays(Habit(), 1)

        assertTrue(actual.isEmpty())
    }

    @Test
    fun getDoneDatesForLastDays_habitWithOldDoneDates_returnEmptyList() {
        val habit = Habit().apply {
            markDone(SimpleDateFormat("dd-MM-yyyy").parse("01-02-2020")!!)
        }

        val actual = LatestDoneDatesHabitUseCase()
            .getDoneDatesForLastDays(habit, 1)

        assertTrue(actual.isEmpty())
    }

    @Test
    fun getDoneDatesForLastDays_zeroNumberOfDays_returnEmptyList() {
        val habit = Habit().apply {
            markDone(Date())
        }

        val actual = LatestDoneDatesHabitUseCase()
            .getDoneDatesForLastDays(habit, 0)

        assertTrue(actual.isEmpty())
    }

    @Test
    fun getDoneDatesForLastDays_habitWithOneRecentDoneDate_returnListWithOneRecentDoneDate() {
        val expectedDate = Date()
        val habit = Habit().apply {
            markDone(
                SimpleDateFormat("dd-MM-yyyy").parse("01-02-2020")!!,
                expectedDate
            )
        }

        val actual = LatestDoneDatesHabitUseCase()
            .getDoneDatesForLastDays(habit, 1)

        assertEquals(1, actual.size)
        assertEquals(expectedDate, actual[0])
    }

    @Test
    fun getDoneDatesForLastDays_habitWithMultipleRecentDoneDate_returnListWithAllDoneDates() {
        val time = Date().time
        val firstDate = Date(time - TimeUnit.HOURS.toMillis(23L))
        val secondDate = Date(time - TimeUnit.MINUTES.toMillis(80L))
        val habit = Habit().apply {
            markDone(firstDate, secondDate)
        }

        val actual = LatestDoneDatesHabitUseCase()
            .getDoneDatesForLastDays(habit, 1)

        assertEquals(2, actual.size)
        assertEquals(listOf(firstDate, secondDate), actual)
    }
}