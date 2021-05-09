package com.example.habittracker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table")
    fun getAll(): LiveData<List<Habit>>

    @Query("SELECT * FROM habit_table WHERE uid IN (:habitIds)")
    fun getAllByIds(habitIds: IntArray): LiveData<List<Habit>>

    @Query("SELECT * FROM habit_table WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): LiveData<Habit>

    @Update
    suspend fun updateAll(vararg habits: Habit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg habits: Habit)

    @Delete
    suspend fun delete(feed: Habit)
}