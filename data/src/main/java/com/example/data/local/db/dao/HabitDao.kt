package com.example.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.data.model.HabitEntity

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table")
    fun getAll(): LiveData<List<HabitEntity>>

    @Query("SELECT * FROM habit_table WHERE uid IN (:habitIds)")
    fun getAllByIds(habitIds: IntArray): LiveData<List<HabitEntity>>

    @Query("SELECT * FROM habit_table WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): LiveData<HabitEntity>

    @Update
    suspend fun updateAll(vararg habits: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg habits: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habit_table")
    suspend fun deleteAll()
}