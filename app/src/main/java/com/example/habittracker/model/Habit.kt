package com.example.habittracker.model

import android.graphics.Color
import java.util.*
import kotlin.random.Random


class Habit(
    var title: String,
    var description: String,
    var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int = generateColor(),
    val uid: String = UUID.randomUUID().toString(),
    var date: Date = Date()
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
        // this.color = state.color
    }
}

private fun generateColor(): Int {
    val color = String.format(
        "#%06X", 0xFFFFFF and Color.argb(
            100,
            Random.nextInt(180, 240),
            Random.nextInt(180, 240),
            Random.nextInt(180, 240)
        )
    )
    return Color.parseColor(color)
}