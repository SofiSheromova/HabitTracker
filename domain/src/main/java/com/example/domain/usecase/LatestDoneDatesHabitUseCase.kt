package com.example.domain.usecase

import com.example.domain.model.Habit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LatestDoneDatesHabitUseCase @Inject constructor() {
    fun getDoneDatesForLastDays(habit: Habit, numberOfDays: Int): List<Date> {
        val startDate = Date(Date().time - TimeUnit.DAYS.toMillis(numberOfDays.toLong()))
        return habit.doneDates.filter { it.after(startDate) }
    }
}