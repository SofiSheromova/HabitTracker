package com.example.habittracker.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table")
    fun getAll(): LiveData<List<HabitRoomModel>>

    @Query("SELECT * FROM habit_table WHERE uid IN (:habitIds)")
    fun getAllByIds(habitIds: IntArray): LiveData<List<HabitRoomModel>>

    @Query("SELECT * FROM habit_table WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): LiveData<HabitRoomModel>

    @Update
    suspend fun updateAll(vararg habits: HabitRoomModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg habits: HabitRoomModel)

    @Delete
    suspend fun delete(habit: HabitRoomModel)

    @Query("DELETE FROM habit_table")
    suspend fun deleteAll()
}