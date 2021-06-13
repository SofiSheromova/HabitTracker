package com.example.data.model

import androidx.room.*
import com.example.data.Constants
import com.example.data.base.ModelEntity
import java.util.*

enum class ActionType(val value: Int) {
    INSERT(0),
    UPDATE(1),
    DELETE(2),
    MARK_DONE(3);

    companion object {
        fun valueOf(value: Int): ActionType? = values().find { it.value == value }
    }
}

@Entity(tableName = Constants.REQUEST_TABLE_NAME)
@TypeConverters(ModificationTypeConverter::class)
data class RequestEntity(
    @ColumnInfo(name = "type") var actionType: ActionType,
    @ColumnInfo(name = "habit_uid") var habitUid: String,
    @ColumnInfo(name = "extra") var habitDoneTime: Long = 0,
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
) : ModelEntity

class ModificationTypeConverter {
    @TypeConverter
    fun fromModificationType(type: ActionType): Int {
        return type.value
    }

    @TypeConverter
    fun toModificationType(value: Int): ActionType {
        return ActionType.valueOf(value) ?: throw IllegalArgumentException()
    }
}
