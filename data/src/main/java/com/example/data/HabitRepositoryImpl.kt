package com.example.data

import android.util.Log
import com.example.data.exception.UnfulfilledRequestException
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.*
import com.example.data.remote.api.HabitApi
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import retrofit2.HttpException
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
        var habitsUpdated = false

        allRequests
            .conflate()
            .onEach {
                fulfillRequests(it)
                if (!habitsUpdated) {
                    habitsUpdated = refresh()
                }
            }
            .retry(RETRIES_FAILED_REQUEST) { e ->
                (e is UnfulfilledRequestException).also {
                    if (it) delay(DELAY_BETWEEN_RETRIES_OF_FAILED_REQUEST)
                }
            }
            .catch { }
            .collect()

        return@withContext
    }

    override suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
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

        for (request in requests) {
            try {
                fulfillRequest(request)
            } catch (e: Exception) {
                Log.d("TAG-NETWORK", "Failure: ${e.message}")

                // Ошибка клиента может возникнуть, когда успели выполнить запрос,
                // но из-за неожиданного завершения работы не успели удалить его из базы
                val isClientError = e is HttpException && e.isClientError()

                if (!isClientError)
                    break
            }

            fulfilledRequests.add(request)
            requestDao.delete(request)
        }

        if (fulfilledRequests.size != requests.size) {
            throw UnfulfilledRequestException()
        }
    }

    private suspend fun fulfillRequest(request: RequestEntity) {
        when (request.actionType) {
            ActionType.INSERT -> {
                habitDao.getById(request.habitUid)?.let { habitEntity ->
                    insertOnServer(habitEntity)
                }
            }
            ActionType.UPDATE -> {
                habitDao.getById(request.habitUid)?.let { habitEntity ->
                    updateOnServer(habitEntity)
                }
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
        val serverUid = habitApi.updateHabit(
            entity.copy().apply { uid = "" }
        ).uid

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

    private fun HttpException.isClientError(): Boolean {
        return this.code() in 400..499
    }

    companion object {
        private const val RETRIES_FAILED_REQUEST: Long = 3
        private const val DELAY_BETWEEN_RETRIES_OF_FAILED_REQUEST: Long = 2000
    }
}
