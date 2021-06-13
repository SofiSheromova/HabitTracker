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
import javax.inject.Singleton

@Entity(tableName = HABIT_TABLE_NAME)
@TypeConverters(DoneDatesConverter::class)
data class HabitEntity(
    @Json(name = "uid") @PrimaryKey var uid: String,
    @Json(name = "title") var title: String,
    @Json(name = "description") var description: String = "",
    @Json(name = "priority") var priority: Int = 0,
    @Json(name = "type") var type: Int = 0,
    @Json(name = "count") var count: Int = 1,
    @Json(name = "frequency") var frequency: Int = 1,
    @Json(name = "color") var color: Int = -1,
    @Json(name = "date") var date: Long = Date().time,
    @Json(name = "done_dates") @ColumnInfo(name = "done_dates")
    var doneDates: MutableList<Long> = mutableListOf()
) : ModelEntity

class DoneDatesConverter {
    @TypeConverter
    fun fromDateList(dates: MutableList<Long>): String {
        return dates.joinToString(",")
    }

    @TypeConverter
    fun toDateList(data: String): MutableList<Long> {
        return data
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.toLong() }
            .toMutableList()
    }
}

@Singleton
class HabitEntityMapper @Inject constructor() : EntityMapper<Habit, HabitEntity> {
    override fun mapToDomain(entity: HabitEntity): Habit = Habit(
        entity.title,
        entity.description,
        Periodicity(entity.count, entity.frequency),
        Type.valueOf(entity.type),
        Priority.valueOf(entity.priority),
        entity.color,
        entity.uid,
        Date(entity.date)
    ).apply {
        this.markDone(*entity.doneDates.map { Date(it) }.toTypedArray())
    }

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
        model.doneDates.map { it.time }.toMutableList()
    )
}
