package com.example.habittracker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.example.habittracker.database.HabitDao
import com.example.habittracker.database.HabitRoomModel
import com.example.habittracker.database.RequestDao
import com.example.habittracker.model.Habit
import com.example.habittracker.model.Periodicity
import com.example.habittracker.model.Priority
import com.example.habittracker.model.Type
import com.example.habittracker.network.HabitApiService
import com.example.habittracker.network.HabitJson
import com.example.habittracker.network.HabitUid
import kotlinx.coroutines.*
import okhttp3.*
import java.util.*

class HabitRepository(
    private val habitDao: HabitDao,
    private val requestDao: RequestDao,
    private val habitApi: HabitApiService,
    private val newCall: (Request) -> Unit
) {
    private val localHabits: LiveData<List<HabitRoomModel>> = habitDao.getAll()
    private val mediator: MediatorLiveData<List<Habit>> = MediatorLiveData<List<Habit>>()

    init {
        mediator.addSource(localHabits) { result ->
            result?.let {
                mediator.value = it.map { habitRoomModel -> habitRoomModel.toHabit() }
            }
        }
    }

    val allHabits: LiveData<List<Habit>> = liveData {
        emitSource(mediator)

        // TODO нормально ли создавать здеть scope? или это делается как-то иначе?
        CoroutineScope(CoroutineName("GetRemoteHabits")).launch {
            refresh()
        }
    }

    suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        val requestModels = requestDao.getAll()
        for (model in requestModels) {
            val request = model.toRequest()
            if (request != null) newCall(request)
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

    suspend fun insert(habit: Habit) = withContext(Dispatchers.IO) {
        val habitModel = habit.toRoomModel()
        habitDao.insertAll(habitModel)

        val serverUid: String
        try {
            serverUid = habitApi.updateHabit(habit.toJson(includeUid = false)).uid
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
            return@withContext
        }

        habitDao.delete(habitModel)
        habitModel.uid = serverUid
        habitDao.insertAll(habitModel)
    }

    suspend fun update(original: Habit, newState: Habit) = withContext(Dispatchers.IO) {
        original.update(newState)
        habitDao.updateAll(original.toRoomModel())

        try {
            habitApi.updateHabit(original.toJson())
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }
    }

    suspend fun delete(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.delete(habit.toRoomModel())

        try {
            habitApi.deleteHabit(HabitUid(habit.uid))
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Failure: ${e.message}")
        }
    }

    private fun Habit.toRoomModel(): HabitRoomModel {
        return HabitRoomModel(
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

    private fun HabitRoomModel.toHabit(): Habit {
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