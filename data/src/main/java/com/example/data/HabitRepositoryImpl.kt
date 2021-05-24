package com.example.data

import android.util.Log
import com.example.data.local.db.dao.HabitDao
import com.example.data.local.db.dao.RequestDao
import com.example.data.model.HabitEntity
import com.example.data.model.HabitJson
import com.example.data.model.HabitUid
import com.example.data.remote.api.HabitApi
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.model.Type
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
    private val newCall: (Request) -> Unit
) : HabitRepository {
    override val allHabits: Flow<List<Habit>> = habitDao.getAll().map { converter(it) }

    private fun converter(habitEntities: List<HabitEntity>): List<Habit> {
        return habitEntities.map { habitRoomModel -> habitRoomModel.toHabit() }
    }

    override suspend fun refreshHabits(): Boolean = withContext(Dispatchers.IO) {
        val requestModels = requestDao.getAll()
        for (model in requestModels) {
            val request = model.toRequest()
            if (request != null)
                newCall(request)
        }

        val remoteHabits: List<HabitJson>
        try {
            remoteHabits = getRemoteHabits()
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext false
        }

        updateLocalHabits(remoteHabits)
        return@withContext true
    }

    private suspend fun getRemoteHabits(): List<HabitJson> = withContext(Dispatchers.IO) {
        habitApi.getHabits()
    }

    private suspend fun updateLocalHabits(newHabits: List<HabitJson>) =
        withContext(Dispatchers.IO) {
            habitDao.deleteAll()
            val habits = newHabits.map { it.toHabit().toRoomModel() }.toTypedArray()
            habitDao.insertAll(*habits)
        }

    override suspend fun insert(habit: Habit) = withContext(Dispatchers.IO) {
        val habitModel = habit.toRoomModel()
        habitDao.insertAll(habitModel)

        val serverUid: String
        try {
            serverUid = habitApi.updateHabit(habit.toJson(includeUid = false)).uid
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
        original.update(newState)
        habitDao.updateAll(original.toRoomModel())

        try {
            habitApi.updateHabit(original.toJson())
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }

    override suspend fun delete(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.delete(habit.toRoomModel())

        try {
            habitApi.deleteHabit(HabitUid(habit.uid))
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }

        return@withContext
    }

    private fun Habit.toRoomModel(): HabitEntity {
        return HabitEntity(
            this.title,
            this.description,
            this.periodicity,
            this.type,
            this.priority,
            this.color,
            this.uid,
            this.date,
            this.doneDate
        )
    }

    private fun HabitEntity.toHabit(): Habit {
        return Habit(
            this.title,
            this.description,
            this.periodicity,
            this.type,
            this.priority,
            this.color,
            this.uid,
            this.date,
            this.doneDates
        )
    }

    private fun HabitJson.toHabit(): Habit {
        return Habit(
            this.title,
            this.description,
            Periodicity(this.count, this.frequency),
            Type.valueOf(this.type),
            Priority.valueOf(this.priority),
            this.color,
            this.uid,
            Date(this.date),
            this.doneDates.map { Date(it) },
        )
    }

    private fun Habit.toJson(includeUid: Boolean = true): HabitJson {
        return HabitJson(
            if (includeUid) this.uid else "",
            this.title,
            this.description,
            this.priority.value,
            this.type.value,
            this.periodicity.repetitionsNumber,
            this.periodicity.daysNumber,
            this.color,
            this.date.time,
        )
    }
}