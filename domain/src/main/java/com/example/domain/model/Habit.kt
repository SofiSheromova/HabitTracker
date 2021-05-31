package com.example.domain.model

import java.util.*

data class Habit(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int = -1,
    val uid: String = UUID.randomUUID().toString(),
    var date: Date = Date(),
) {
    private val _doneDates: MutableList<Date> = mutableListOf()
    val doneDates: List<Date> = _doneDates

    constructor() : this(
        "",
        "",
        Periodicity(1, 1),
        Type.GOOD,
        Priority.LOW,
    )

    fun markDone(vararg dates: Date) {
        _doneDates.addAll(dates)
    }
}
