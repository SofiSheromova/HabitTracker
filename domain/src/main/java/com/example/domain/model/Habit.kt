package com.example.domain.model

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
    val doneDate: List<Date> = listOf()
) {
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
}
