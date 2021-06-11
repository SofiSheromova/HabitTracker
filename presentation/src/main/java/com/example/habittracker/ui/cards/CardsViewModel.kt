package com.example.habittracker.ui.cards

import androidx.lifecycle.*
import com.example.domain.model.Habit
import com.example.domain.usecase.GetAllHabitsUseCase
import com.example.domain.usecase.HabitFulfillmentReportUseCase
import com.example.domain.usecase.MarkHabitDoneUseCase
import com.example.domain.usecase.RefreshHabitsUseCase
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.model.HabitFulfillmentReportFormatter
import com.example.habittracker.util.Event
import kotlinx.coroutines.launch

class CardsViewModel(
    private val displayOptions: DisplayOptions,
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val refreshHabitsUseCase: RefreshHabitsUseCase,
    private val markHabitDoneUseCase: MarkHabitDoneUseCase,
    private val reportUseCase: HabitFulfillmentReportUseCase,
    private val reportFormatter: HabitFulfillmentReportFormatter
) : ViewModel() {
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
        val report = reportUseCase.getHabitFulfillmentReport(habit)
        return reportFormatter.reportToString(report)
    }

    private fun createHabitsMediator(): MediatorLiveData<List<Habit>> {
        val mediator = MediatorLiveData<List<Habit>>()
        mediator.addSource(_allHabitsLiveData) {
            mediator.value = getHabits()
        }
        return mediator
    }

    interface Factory : ViewModelProvider.Factory {
        var displayOptions: DisplayOptions

        fun setDisplayOptions(displayOptions: DisplayOptions): Factory {
            this.displayOptions = displayOptions
            return this
        }
    }
}
