package com.example.data.model

import androidx.room.*
import com.example.data.Constants.HABIT_TABLE_NAME
import com.example.data.base.EntityMapper
import com.example.data.base.ModelEntity
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.model.Type
import com.squareup.moshi.Json
import java.util.*
import javax.inject.Inject

@Entity(tableName = HABIT_TABLE_NAME)
@TypeConverters(DoneDatesConverter::class)
class HabitEntity(
    @Json(name = "uid") @PrimaryKey var uid: String,
    @Json(name = "title") var title: String,
    @Json(name = "description") var description: String = "",
    @Json(name = "priority") var priority: Int = 0,
    @Json(name = "type") var type: Int = 0,
    @Json(name = "count") var count: Int = 1,
    @Json(name = "frequency") var frequency: Int = 1,
    @Json(name = "color") var color: Int = -1,
    @Json(name = "date") var date: Long = Date().time,
    @Json(name = "done_dates") @ColumnInfo(name = "done_dates") var doneDates: List<Long> = listOf()
) : ModelEntity() {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(HabitEntity::class.java.getName()).append('@')
            .append(Integer.toHexString(System.identityHashCode(this))).append('[')
        sb.append("uid")
        sb.append('=')
        sb.append(uid)
        sb.append(',')
        sb.append("title")
        sb.append('=')
        sb.append(title)
        sb.append(',')
        sb.append("description")
        sb.append('=')
        sb.append(description)
        sb.append(',')
        sb.append("priority")
        sb.append('=')
        sb.append(priority)
        sb.append(',')
        sb.append("type")
        sb.append('=')
        sb.append(type)
        sb.append(',')
        sb.append("count")
        sb.append('=')
        sb.append(count)
        sb.append(',')
        sb.append("frequency")
        sb.append('=')
        sb.append(frequency)
        sb.append(',')
        sb.append("color")
        sb.append('=')
        sb.append(color)
        sb.append(',')
        sb.append("date")
        sb.append('=')
        sb.append(date)
        sb.append(',')
        sb.append("doneDates")
        sb.append('=')
        sb.append(doneDates)
        sb.append(',')
        if (sb[sb.length - 1] == ',') {
            sb.setCharAt(sb.length - 1, ']')
        } else {
            sb.append(']')
        }
        return sb.toString()
    }
}

class DoneDatesConverter {
    @TypeConverter
    fun fromDateList(dates: List<Long>): String {
        return dates.joinToString(",")
    }

    @TypeConverter
    fun toDateList(data: String): List<Long> {
        return data.split(",").filter { it.isNotEmpty() }.map { it.toLong() }
    }
}

class HabitEntityMapper @Inject constructor() : EntityMapper<Habit, HabitEntity> {
    override fun mapToDomain(entity: HabitEntity): Habit = Habit(
        entity.title,
        entity.description,
        Periodicity(entity.count, entity.frequency),
        Type.valueOf(entity.type),
        Priority.valueOf(entity.priority),
        entity.color,
        entity.uid,
        Date(entity.date),
        entity.doneDates.map { Date(it) },
    )

    override fun mapToEntity(model: Habit): HabitEntity = HabitEntity(
        model.uid,
        model.title,
        model.description,
        model.priority.value,
        model.type.value,
        model.periodicity.repetitionsNumber,
        model.periodicity.daysNumber,
        model.color,
        model.date.time,
    )
}