package com.example.habittracker.ui.cards

import androidx.lifecycle.*
import com.example.domain.model.DisplayOptions
import com.example.domain.model.Habit
import com.example.domain.usecase.GetAllHabitsUseCase
import com.example.domain.usecase.RefreshHabitsUseCase
import com.example.habittracker.util.Event
import kotlinx.coroutines.launch

class CardsViewModel(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val refreshHabitsUseCase: RefreshHabitsUseCase,
    private val displayOptions: DisplayOptions
) : ViewModel() {
    private val _allHabitsLiveData: LiveData<List<Habit>> =
        getAllHabitsUseCase.getAll().asLiveData()

    private val _habitsLiveData: MediatorLiveData<List<Habit>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Habit>> = _habitsLiveData

    private val _refreshLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val refreshLiveData: LiveData<Event<Boolean>> = _refreshLiveData

    private val _networkError: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val networkError: LiveData<Event<Boolean>> = _networkError

    private fun getHabits(): List<Habit> {
        return displayOptions.filter(_allHabitsLiveData.value)
//        return getAllHabitsUseCase.getAll(displayOptions).asLiveData().value
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

    private fun createHabitsMediator(): MediatorLiveData<List<Habit>> {
        val mediator = MediatorLiveData<List<Habit>>()
        mediator.addSource(_allHabitsLiveData) {
            mediator.value = getHabits()
        }
        return mediator
    }
}

class CardsViewModelFactory(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val refreshHabitsUseCase: RefreshHabitsUseCase,
    private val options: DisplayOptions? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(
                getAllHabitsUseCase,
                refreshHabitsUseCase,
                options ?: DisplayOptions()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}