package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.model.HabitFulfillmentReport
import javax.inject.Inject

class HabitFulfillmentReportUseCase @Inject constructor(
    private val latestDoneDatesHabitUseCase: LatestDoneDatesHabitUseCase
) {
    fun getHabitFulfillmentReport(habit: Habit): HabitFulfillmentReport {
        val dates = latestDoneDatesHabitUseCase
            .getDoneDatesForLastDays(habit, habit.periodicity.daysNumber)
        val difference = habit.periodicity.repetitionsNumber - dates.size

        return HabitFulfillmentReport(habit.type, difference)
    }
}
