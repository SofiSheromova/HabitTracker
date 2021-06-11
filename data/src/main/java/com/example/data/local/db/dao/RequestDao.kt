package com.example.data.local.db.dao

import androidx.room.*
import com.example.data.model.RequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Query("SELECT * FROM request_table")
    fun getAll(): Flow<List<RequestEntity>>

    @Update
    suspend fun updateAll(vararg requests: RequestEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg requests: RequestEntity)

    @Delete
    suspend fun delete(request: RequestEntity)

    @Query("DELETE from request_table WHERE id IN (:requestIds)")
    suspend fun delete(vararg requestIds: String)

    @Query("DELETE FROM request_table")
    suspend fun deleteAll()
}