package com.example.habittracker.database

import androidx.room.*

@Dao
interface RequestDao {
    @Query("SELECT * FROM request_table")
    fun getAll(): List<RequestModel>

    @Update
    suspend fun updateAll(vararg requests: RequestModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg requests: RequestModel)

    @Delete
    suspend fun delete(request: RequestModel)

    @Query("DELETE FROM request_table")
    suspend fun deleteAll()
}