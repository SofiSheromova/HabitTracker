package com.example.habittracker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CardDao {
    @Query("SELECT * FROM card_table")
    fun getAll(): LiveData<List<Card>>

    @Query("SELECT * FROM card_table WHERE id IN (:cardIds)")
    fun getAllByIds(cardIds: IntArray): LiveData<List<Card>>

    @Query("SELECT * FROM card_table WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): LiveData<Card>

    @Update
    suspend fun updateAll(vararg cards: Card)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg cards: Card)

    @Delete
    suspend fun delete(feed: Card)
}