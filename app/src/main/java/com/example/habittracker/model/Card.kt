package com.example.habittracker.model

import android.graphics.Color
import androidx.room.*
import kotlin.random.Random


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

@Entity(tableName = "card_table")
@TypeConverters(HabitTypeConverter::class)
class Card(
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @Embedded var periodicity: Periodicity,
    @ColumnInfo val type: Type,
    @ColumnInfo val priority: Int,
    @ColumnInfo var color: String? = generateColor()
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