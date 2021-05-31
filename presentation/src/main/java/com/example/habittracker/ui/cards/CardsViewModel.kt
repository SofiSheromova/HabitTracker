package com.example.habittracker.ui.cards

import androidx.lifecycle.*
import com.example.habittracker.model.DisplayOptions
import com.example.domain.model.Habit
import com.example.domain.model.Type
import com.example.domain.usecase.GetAllHabitsUseCase
import com.example.domain.usecase.LatestDoneDatesHabitUseCase
import com.example.domain.usecase.MarkHabitDoneUseCase
import com.example.domain.usecase.RefreshHabitsUseCase
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.util.Event
import kotlinx.coroutines.launch

class CardsViewModel(
    application: HabitTrackerApplication,
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val refreshHabitsUseCase: RefreshHabitsUseCase,
    private val markHabitDoneUseCase: MarkHabitDoneUseCase,
    private val latestDoneDatesHabitUseCase: LatestDoneDatesHabitUseCase,
    private val displayOptions: DisplayOptions,
) : AndroidViewModel(application) {
    private val _allHabitsLiveData: LiveData<List<Habit>> =
        getAllHabitsUseCase.getAll().asLiveData()

    private val _habitsLiveData: MediatorLiveData<List<Habit>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Habit>> = _habitsLiveData

    private val _refreshLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val refreshLiveData: LiveData<Event<Boolean>> = _refreshLiveData

    private val _networkError: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val networkError: LiveData<Event<Boolean>> = _networkError

    private val _habitFulfillmentReport: MutableLiveData<Event<String>> = MutableLiveData()
    val habitFulfillmentReport: LiveData<Event<String>> = _habitFulfillmentReport

    private fun getHabits(): List<Habit> {
        return displayOptions.filter(_allHabitsLiveData.value)
    }

    fun refreshHabits() {
        _habitsLiveData.value = getHabits()
    }

    fun refresh() = viewModelScope.launch {
        _refreshLiveData.value = Event(true)
        try {
            if (!refreshHabitsUseCase.refresh()) _networkError.value = Event(true)
        } catch (e: Exception) {
            _networkError.value = Event(true)
        }
        _refreshLiveData.value = Event(false)
    }

    fun markDone(habit: Habit) = viewModelScope.launch {
        markHabitDoneUseCase.markDone(habit)

        val report = getHabitFulfillmentReport(habit)
        _habitFulfillmentReport.value = Event(report)
    }

    private fun getHabitFulfillmentReport(habit: Habit): String {
        val dates = latestDoneDatesHabitUseCase
            .getDoneDatesForLastDays(habit, habit.periodicity.daysNumber)
        val difference = habit.periodicity.repetitionsNumber - dates.size
        val context = getApplication<HabitTrackerApplication>().applicationContext
        return when {
            habit.type == Type.GOOD && difference <= 0 ->
                context.resources.getString(R.string.breathtaking)
            habit.type == Type.GOOD ->
                context.resources.getQuantityString(
                    R.plurals.still_need_to_be_done,
                    difference,
                    difference
                )
            habit.type == Type.BAD && difference <= 0 ->
                context.resources.getString(R.string.stop_it)
            else -> context.resources.getQuantityString(
                R.plurals.can_be_done,
                difference,
                difference
            )
        }
    }

    private fun createHabitsMediator(): MediatorLiveData<List<Habit>> {
        val mediator = MediatorLiveData<List<Habit>>()
        mediator.addSource(_allHabitsLiveData) {
            mediator.value = getHabits()
        }
        return mediator
    }
}

class CardsViewModelFactory(
    private val application: HabitTrackerApplication,
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val refreshHabitsUseCase: RefreshHabitsUseCase,
    private val markHabitDoneUseCase: MarkHabitDoneUseCase,
    private val latestDoneDatesHabitUseCase: LatestDoneDatesHabitUseCase,
    private val options: DisplayOptions? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(
                application,
                getAllHabitsUseCase,
                refreshHabitsUseCase,
                markHabitDoneUseCase,
                latestDoneDatesHabitUseCase,
                options ?: DisplayOptions()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}