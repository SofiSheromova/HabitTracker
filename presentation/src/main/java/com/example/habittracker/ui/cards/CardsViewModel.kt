package com.example.habittracker.ui.cards

import androidx.lifecycle.*
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import com.example.habittracker.middleware.Event
import com.example.habittracker.ui.home.DisplayOptions
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: HabitRepository,
    private val displayOptions: DisplayOptions
) : ViewModel() {
    private val _allHabitsLiveData: LiveData<List<Habit>> = repository.allHabits.asLiveData()

    private val _habitsLiveData: MediatorLiveData<List<Habit>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Habit>> = _habitsLiveData

    private val _refreshLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val refreshLiveData: LiveData<Event<Boolean>> = _refreshLiveData

    private val _networkError: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val networkError: LiveData<Event<Boolean>> = _networkError

    private fun getHabits(): List<Habit> {
        return displayOptions.filter(_allHabitsLiveData.value)
    }

    fun refreshHabits() {
        _habitsLiveData.value = getHabits()
    }

    fun refresh() = viewModelScope.launch {
        _refreshLiveData.value = Event(true)
        try {
            if (!repository.refreshHabits()) _networkError.value = Event(true)
        } catch (e: Exception) {
            _networkError.value = Event(true)
        }
        _refreshLiveData.value = Event(false)
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
    private val repository: HabitRepository,
    private val options: DisplayOptions? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(repository, options ?: DisplayOptions()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}