package com.example.habittracker.model

import androidx.room.*
import java.util.*


@Entity(tableName = "habit_table")
@TypeConverters(HabitTypeConverter::class, HabitPriorityConverter::class, DateConverter::class)
class HabitRoomModel(
    var title: String,
    var description: String,
    @Embedded var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int,
    @PrimaryKey val uid: String,
    var date: Date
)

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
