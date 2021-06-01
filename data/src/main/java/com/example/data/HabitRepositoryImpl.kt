package com.example.data

import android.util.Log
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.*
import com.example.data.remote.api.HabitApi
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.*
import java.util.*

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val requestDao: RequestDao,
    private val habitApi: HabitApi,
    private val newCall: (Request) -> Unit,
    private val habitEntityMapper: HabitEntityMapper,
    private val requestEntityMapper: RequestEntityMapper,
) : HabitRepository {

    override val allHabits: Flow<List<Habit>> = habitDao.getAll().map { converter(it) }

    private fun converter(habitEntities: List<HabitEntity>): List<Habit> {
        return habitEntities.map { habitEntityMapper.mapToDomain(it) }
    }

    override suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        try {
            habitApi.getHabits()
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext false
        }

        return@withContext true
    }

    override suspend fun start() = withContext(Dispatchers.IO) {
        val entities = requestDao.getAll()
        for (entity in entities) {
            val request = try {
                requestEntityMapper.mapToDomain(entity)
            } catch (e: Exception) {
                Log.d("TAG-NETWORK", "Cast exception RequestModel to Request")
                null
            }
            request?.let(newCall)
        }

        val remoteHabits: List<HabitEntity>
        try {
            remoteHabits = getRemoteHabits()
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext
        }

        updateLocalHabits(remoteHabits)
        return@withContext
    }

    private suspend fun getRemoteHabits(): List<HabitEntity> = withContext(Dispatchers.IO) {
        habitApi.getHabits()
    }

    private suspend fun updateLocalHabits(
        newHabits: List<HabitEntity>
    ) = withContext(Dispatchers.IO) {
        habitDao.deleteAll()
        habitDao.insertAll(*newHabits.toTypedArray())
    }

    override suspend fun insert(habit: Habit) = withContext(Dispatchers.IO) {
        val entity = habitEntityMapper.mapToEntity(habit)
        habitDao.insertAll(entity)

        val serverUid: String
        try {
            val habitUid = habitApi.updateHabit(
                habitEntityMapper.mapToEntity(habit).apply { uid = "" }
            )
            serverUid = habitUid.uid
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext
        }

        updateUid(entity, serverUid)
    }

    private suspend fun updateUid(habitModel: HabitEntity, uid: String) {
        habitDao.delete(habitModel)
        habitModel.uid = uid
        habitDao.insertAll(habitModel)
    }

    override suspend fun update(habit: Habit) = withContext(Dispatchers.IO) {
        val entity = habitEntityMapper.mapToEntity(habit)

        habitDao.updateAll(entity)

        try {
            habitApi.updateHabit(entity)
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }

    override suspend fun delete(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.delete(habitEntityMapper.mapToEntity(habit))

        try {
            habitApi.deleteHabit(HabitUid(habit.uid))
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }

    override suspend fun markDone(habit: Habit, time: Long) = withContext(Dispatchers.IO) {
        habitDao.updateAll(habitEntityMapper.mapToEntity(habit))

        try {
            habitApi.markHabitDone(HabitDone(habit.uid, time))
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }
}