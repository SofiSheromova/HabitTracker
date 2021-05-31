package com.example.data

import android.util.Log
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.HabitDone
import com.example.data.model.HabitEntity
import com.example.data.model.HabitEntityMapper
import com.example.data.model.HabitUid
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
    private val habitEntityMapper: HabitEntityMapper
) : HabitRepository {

    override val allHabits: Flow<List<Habit>> = habitDao.getAll().map { converter(it) }

    private fun converter(habitEntities: List<HabitEntity>): List<Habit> {
        return habitEntities.map { habitEntityMapper.mapToDomain(it) }
    }

    override suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        val requestModels = requestDao.getAll()
        for (model in requestModels) {
            val request = model.toRequest()
            if (request != null)
                newCall(request)
        }

        val remoteHabits: List<HabitEntity>
        try {
            remoteHabits = getRemoteHabits()
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext false
        }

        updateLocalHabits(remoteHabits)
        return@withContext true
    }

    private suspend fun getRemoteHabits(): List<HabitEntity> = withContext(Dispatchers.IO) {
        habitApi.getHabits()
    }

    private suspend fun updateLocalHabits(newHabits: List<HabitEntity>) =
        withContext(Dispatchers.IO) {
            habitDao.deleteAll()
            val habits = newHabits.toTypedArray()
            habitDao.insertAll(*habits)
        }

    override suspend fun insert(habit: Habit) = withContext(Dispatchers.IO) {
        val habitModel = habitEntityMapper.mapToEntity(habit)
        habitDao.insertAll(habitModel)

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

        updateUid(habitModel, serverUid)
    }

    private suspend fun updateUid(habitModel: HabitEntity, uid: String) {
        habitDao.delete(habitModel)
        habitModel.uid = uid
        habitDao.insertAll(habitModel)
    }

    override suspend fun update(original: Habit, newState: Habit) = withContext(Dispatchers.IO) {
        val habit = Habit(
            newState.title,
            newState.description,
            newState.periodicity,
            newState.type,
            newState.priority,
            // newState.color,
            original.color,
            original.uid,
            Date(),
            doneDates = newState.doneDates
        )
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

    override suspend fun markDone(habit: Habit) = withContext(Dispatchers.IO) {
        val time = habit.markDone().time
        Log.d("TAG-DONE", habit.doneDates.toString())

        habitDao.updateAll(habitEntityMapper.mapToEntity(habit))

        try {
            habitApi.markHabitDone(HabitDone(habit.uid, time))
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }
}