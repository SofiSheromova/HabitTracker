package com.example.data.local.db.dao

import androidx.room.*
import com.example.data.model.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table ORDER BY date DESC")
    fun getAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_table WHERE uid LIKE :uid LIMIT 1")
    fun getById(uid: String): HabitEntity?

    @Update
    suspend fun updateAll(vararg habits: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg habits: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habit_table")
    suspend fun deleteAll()
}