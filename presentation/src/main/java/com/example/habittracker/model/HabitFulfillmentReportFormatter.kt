package com.example.habittracker.model

import android.content.res.Resources
import com.example.domain.model.HabitFulfillmentReport
import com.example.domain.model.Type
import com.example.habittracker.R
import javax.inject.Inject

class HabitFulfillmentReportFormatter @Inject constructor(private val resources: Resources) {
    fun reportToString(report: HabitFulfillmentReport): String {
        return when {
            report.type == Type.GOOD && report.difference <= 0 ->
                resources.getString(R.string.breathtaking)
            report.type == Type.GOOD ->
                resources.getQuantityString(
                    R.plurals.still_need_to_be_done,
                    report.difference,
                    report.difference
                )
            report.type == Type.BAD && report.difference <= 0 ->
                resources.getString(R.string.stop_it)
            else -> resources.getQuantityString(
                R.plurals.can_be_done,
                report.difference,
                report.difference
            )
        }
    }
}