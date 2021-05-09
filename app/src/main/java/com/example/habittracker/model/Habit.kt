package com.example.habittracker.model

import android.graphics.Color
import androidx.room.*
import java.util.*
import kotlin.random.Random


@Entity(tableName = "habit_table")
@TypeConverters(HabitTypeConverter::class, HabitPriorityConverter::class, DateConverter::class)
class Habit(
    var title: String,
    var description: String,
    @Embedded var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int = generateColor(),
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
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

class HabitTypeConverter {
    @TypeConverter
    fun fromType(type: Type): Int {
        return type.value
    }

    @TypeConverter
    fun toType(data: Int): Type {
        return Type.valueOf(data)
    }
}

class HabitPriorityConverter {
    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.value
    }

    @TypeConverter
    fun toPriority(data: Int): Priority {
        return Priority.valueOf(data)
    }
}

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(data: Long): Date {
        return Date(data)
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