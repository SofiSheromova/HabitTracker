package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.model.HabitFulfillmentReport
import com.example.domain.model.Periodicity
import com.example.domain.model.Type
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class HabitFulfillmentReportUseCaseTest {
    private val latestDoneDates = LatestDoneDatesHabitUseCase()

    @Test
    fun getHabitFulfillmentReport_goodHabitFulfilled_returnZeroDifference() {
        val habit = Habit().apply { markDone(Date()) }
        val actual = HabitFulfillmentReportUseCase(latestDoneDates)
            .getHabitFulfillmentReport(habit)

        assertEquals(HabitFulfillmentReport(Type.GOOD, 0), actual)
    }

    @Test
    fun getHabitFulfillmentReport_badHabitFulfilled_returnZeroDifference() {
        val habit = Habit().apply {
            type = Type.BAD
            markDone(Date())
        }
        val actual = HabitFulfillmentReportUseCase(latestDoneDates)
            .getHabitFulfillmentReport(habit)

        assertEquals(HabitFulfillmentReport(Type.BAD, 0), actual)
    }

    @Test
    fun getHabitFulfillmentReport_goodHabitNotFulfilledEnoughTimes_returnDifferenceIsPositiveOne() {
        val habit = Habit().apply {
            periodicity = Periodicity(2, 1)
            markDone(Date())
        }

        val actual = HabitFulfillmentReportUseCase(latestDoneDates)
            .getHabitFulfillmentReport(habit)

        assertEquals(HabitFulfillmentReport(Type.GOOD, 1), actual)
    }

    @Test
    fun getHabitFulfillmentReport_goodHabitOverfulfilled_returnDifferenceIsNegativeOne() {
        val habit = Habit().apply {
            markDone(Date(), Date())
        }

        val actual = HabitFulfillmentReportUseCase(latestDoneDates)
            .getHabitFulfillmentReport(habit)

        assertEquals(HabitFulfillmentReport(Type.GOOD, -1), actual)
    }
}