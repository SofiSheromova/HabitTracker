package com.example.data

import android.util.Log
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.*
import com.example.data.remote.api.HabitApi
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.*

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val requestDao: RequestDao,
    private val habitApi: HabitApi,
    private val habitEntityMapper: HabitEntityMapper,
) : HabitRepository {

    override val allHabits: Flow<List<Habit>> = habitDao.getAll().map { converter(it) }

    private var completedCount: Int = 0
    private val allRequests: Flow<List<RequestEntity>> = requestDao.getAll()

    private fun converter(habitEntities: List<HabitEntity>): List<Habit> {
        return habitEntities.map { habitEntityMapper.mapToDomain(it) }
    }

    override suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        val uncompletedRequests = allRequests.first().uncompleted()
        if (uncompletedRequests.isNotEmpty()) {
            return@withContext false
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

    override suspend fun start() = withContext(Dispatchers.IO) {
        requestDao.deleteAll()
        this.launch {
            allRequests.collect {
                val uncompletedRequests = it.uncompleted()
                for (request in uncompletedRequests) {
                    try {
                        when (request.actionType) {
                            ActionType.INSERT -> {
                                val habitEntity = habitDao.getById(request.habitUid)
                                insertOnServer(habitEntity)
                            }
                            ActionType.UPDATE -> {
                                val habitEntity = habitDao.getById(request.habitUid)
                                updateOnServer(habitEntity)
                            }
                            ActionType.DELETE -> {
                                deleteOnServer(request.habitUid)
                            }
                            ActionType.MARK_DONE -> {
                                markDoneOnServer(
                                    request.habitUid,
                                    request.extraInformation.toLong()
                                )
                            }
                        }
                        completedCount++
                    } catch (e: Exception) {
                        Log.d("TAG-NETWORK", "Failure: ${e.message}")
                        break
                    }
                }

                Log.d(
                    "TAG-E",
                    "all size = ${it.size}, uncompleted size = ${uncompletedRequests.size}"
                )
            }
        }

        refresh()

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

        requestDao.insertAll(RequestEntity(ActionType.INSERT, habit.uid))
    }

    override suspend fun update(habit: Habit) = withContext(Dispatchers.IO) {
        val entity = habitEntityMapper.mapToEntity(habit)

        habitDao.updateAll(entity)

        val request = findByHabit(habit)
        if (request == null) {
            requestDao.insertAll(RequestEntity(ActionType.UPDATE, habit.uid))
        }

        return@withContext
    }

    override suspend fun delete(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.delete(habitEntityMapper.mapToEntity(habit))

        val request = findByHabit(habit)
        if (request == null) {
            requestDao.insertAll(RequestEntity(ActionType.DELETE, habit.uid))
        } else {
            when (request.actionType) {
                ActionType.INSERT -> {
                    requestDao.delete(request)
                }
                ActionType.UPDATE -> {
                    requestDao.updateAll(
                        RequestEntity(ActionType.DELETE, habit.uid, request.id)
                    )
                }
                else -> {
                }
            }
        }

        return@withContext
    }

    override suspend fun markDone(habit: Habit, time: Long) = withContext(Dispatchers.IO) {
        habitDao.updateAll(habitEntityMapper.mapToEntity(habit))

        val request = findByHabit(habit)
        if (request == null) {
            requestDao.insertAll(
                RequestEntity(ActionType.MARK_DONE, habit.uid, extraInformation = time.toString())
            )
        }

        return@withContext
    }

    private suspend fun insertOnServer(entity: HabitEntity) {
        val copyEntity = HabitEntity(
            "", entity.title, entity.description, entity.priority, entity.type,
            entity.count, entity.frequency, entity.color, entity.date, entity.doneDates
        )

        val serverUid = habitApi.updateHabit(copyEntity).uid

        updateUid(entity, serverUid)
    }

    private suspend fun updateUid(habitEntity: HabitEntity, uid: String) {
        habitDao.delete(habitEntity)
        habitEntity.uid = uid
        habitDao.insertAll(habitEntity)
    }

    private suspend fun updateOnServer(entity: HabitEntity) {
        habitApi.updateHabit(entity)
    }

    private suspend fun deleteOnServer(uid: String) {
        habitApi.deleteHabit(HabitUid(uid))
    }

    private suspend fun markDoneOnServer(uid: String, time: Long) {
        habitApi.markHabitDone(HabitDone(uid, time))
    }

    private fun List<RequestEntity>.uncompleted(): List<RequestEntity> {
        return this.subList(completedCount, this.size)
    }

//    private fun List<RequestEntity>.completed(): List<RequestEntity> {
//        return this.subList(0, completedCount)
//    }

    private suspend fun findByHabit(habit: Habit): RequestEntity? {
        return allRequests.firstOrNull()
            ?.uncompleted()
            ?.find { it.habitUid == habit.uid }
    }
}
