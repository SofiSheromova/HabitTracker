package com.example.habittracker.model

import android.graphics.Color
import androidx.room.*
import kotlin.random.Random


@Entity(tableName = "habit_table")
@TypeConverters(HabitTypeConverter::class)
class Habit(
    @ColumnInfo var title: String,
    @ColumnInfo var description: String,
    @Embedded var periodicity: Periodicity,
    @ColumnInfo var type: Type,
    @ColumnInfo var priority: Int,
    @ColumnInfo var color: String = generateColor()
) {
    @PrimaryKey(autoGenerate = true)
    // TODO из-за базы данных это var, что печально
    var id: Long = 0

    constructor() : this(
        "",
        "",
        Periodicity(1, 1),
        Type.GOOD,
        1,
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

private fun generateColor(): String {
    return String.format(
        "#%06X", 0xFFFFFF and Color.argb(
            100,
            Random.nextInt(180, 240),
            Random.nextInt(180, 240),
            Random.nextInt(180, 240)
        )
    )
}