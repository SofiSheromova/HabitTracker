package com.example.domain.model

import com.example.domain.base.Model
import java.time.Period
import java.util.*

class Habit(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int = -1,
    val uid: String = UUID.randomUUID().toString(),
    var date: Date = Date(),
    doneDates: List<Date> = listOf()
) : Model() {
    private val _doneDates = doneDates.toMutableList()
    val doneDates: List<Date> = _doneDates

    constructor() : this(
        "",
        "",
        Periodicity(1, 1),
        Type.GOOD,
        Priority.LOW,
    )

    fun update(state: Habit) {
        this.title = state.title
        this.description = state.description
        this.periodicity = state.periodicity
        this.type = state.type
        this.priority = state.priority
        this.date = Date()
        // this.color = state.color
    }

    fun markDone(): Date {
        val date = Date()
        _doneDates.add(date)
        return date
    }

    fun getRecentRepetitions(period: Period): List<Date> {
        val startDate = Date(Date().time - period.days * 86400000)
        return doneDates.filter { it.after(startDate) }
    }
}
