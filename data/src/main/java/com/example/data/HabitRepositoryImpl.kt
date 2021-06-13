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

    private val allRequests: Flow<List<RequestEntity>> = requestDao.getAll()

    override val allHabits: Flow<List<Habit>> = habitDao.getAll().map {
        it.map { entity -> habitEntityMapper.mapToDomain(entity) }
    }

    override suspend fun start() = withContext(Dispatchers.IO) {
        refresh()

        allRequests
            .conflate()
            .collect {
                Log.d("TAG-E", "------Collect: ${it.size}")
                fulfillRequests(it)
            }

        return@withContext
    }

    override suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        fulfillRequests(allRequests.first())

        if (allRequests.first().isNotEmpty()) {
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

        val request = findRequestByHabit(habit)
        if (request == null) {
            requestDao.insertAll(RequestEntity(ActionType.UPDATE, habit.uid))
        }

        return@withContext
    }

    override suspend fun delete(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.delete(habitEntityMapper.mapToEntity(habit))

        val request = findRequestByHabit(habit)
        if (request == null) {
            requestDao.insertAll(RequestEntity(ActionType.DELETE, habit.uid))
        } else {
            when (request.actionType) {
                ActionType.INSERT -> {
                    requestDao.delete(request)
                }
                ActionType.UPDATE -> {
                    requestDao.updateAll(
                        RequestEntity(ActionType.DELETE, habit.uid, id = request.id)
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

        val request = findRequestByHabit(habit)
        if (request == null || request.actionType != ActionType.INSERT) {
            requestDao.insertAll(RequestEntity(ActionType.MARK_DONE, habit.uid, time))
        }

        return@withContext
    }

    private suspend fun findRequestByHabit(habit: Habit): RequestEntity? {
        return allRequests.first()
            .find { it.habitUid == habit.uid }
    }

    private suspend fun fulfillRequests(requests: List<RequestEntity>) {
        val fulfilledRequests = mutableListOf<RequestEntity>()

        Log.d("TAG-E", "fulfillRequests count = ${requests.size}")
        for (request in requests) {
            try {
                fulfillRequest(request)
                fulfilledRequests.add(request)
            } catch (e: Exception) {
                Log.d("TAG-NETWORK", "Failure: ${e.message}")
                break
            }
        }

        if (fulfilledRequests.isNotEmpty())
            requestDao.delete(*fulfilledRequests.map { it.id }.toTypedArray())
    }

    private suspend fun fulfillRequest(request: RequestEntity) {
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
                    request.habitDoneTime
                )
            }
        }
    }

    private suspend fun insertOnServer(entity: HabitEntity) {
        // TODO если бы HabitEntity было дата классом, то было бы проще
        val copyEntity = HabitEntity(
            "", entity.title, entity.description, entity.priority, entity.type,
            entity.count, entity.frequency, entity.color, entity.date, entity.doneDates
        )

        val serverUid = habitApi.updateHabit(copyEntity).uid

        for (date in entity.doneDates) {
            markDoneOnServer(serverUid, date)
        }

        updateLocalUid(entity, serverUid)
    }

    private suspend fun updateLocalUid(habitEntity: HabitEntity, uid: String) {
        habitDao.delete(habitEntity)
        habitDao.insertAll(habitEntity.apply { this.uid = uid })
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
}
