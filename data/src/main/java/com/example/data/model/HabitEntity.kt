package com.example.data.model

import androidx.room.*
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.model.Type
import java.util.*


@Entity(tableName = "habit_table")
@TypeConverters(
    HabitTypeConverter::class,
    HabitPriorityConverter::class,
    DateConverter::class,
    DoneDatesConverter::class
)
class HabitEntity(
    var title: String,
    var description: String,
    @Embedded var periodicity: Periodicity,
    var type: Type,
    var priority: Priority,
    var color: Int,
    @PrimaryKey var uid: String,
    var date: Date,
    @ColumnInfo(name = "done_dates") var doneDates: List<Date>
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

class DoneDatesConverter {
    @TypeConverter
    fun fromDateList(dates: List<Date>): String {
        return dates.map { it.time }.joinToString(",")
    }

    @TypeConverter
    fun toDateList(data: String): List<Date> {
        return data.split(",").filter { it.isNotEmpty() }.map { Date(it.toLong()) }
    }
}
