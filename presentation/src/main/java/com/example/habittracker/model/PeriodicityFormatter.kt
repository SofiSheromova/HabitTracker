package com.example.habittracker.model

import android.content.res.Resources
import com.example.domain.model.Periodicity
import com.example.habittracker.R
import javax.inject.Inject

class PeriodicityFormatter @Inject constructor(private val resources: Resources) {
    fun periodicityToString(periodicity: Periodicity): String {
        return if (periodicity.repetitionsNumber == 1 && periodicity.daysNumber == 1) {
            resources.getString(R.string.everyday)
        } else {
            resources.getQuantityString(
                R.plurals.times_in,
                periodicity.repetitionsNumber,
                periodicity.repetitionsNumber
            ) + " " + resources.getQuantityString(
                R.plurals.days,
                periodicity.daysNumber,
                periodicity.daysNumber
            )
        }
    }
}