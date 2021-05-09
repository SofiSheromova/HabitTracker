package com.example.habittracker.ui.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.model.Habit
import com.example.habittracker.HabitRepository
import com.example.habittracker.ui.home.DisplayOptions

class CardsViewModel(
    private val repository: HabitRepository,
    private val displayOptions: DisplayOptions
) : ViewModel() {
    private val _habitsLiveData: MediatorLiveData<List<Habit>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Habit>> = _habitsLiveData

    private fun getHabits(): List<Habit> {
        return displayOptions.filter(repository.allHabits.value)
    }

    fun refreshHabits() {
        _habitsLiveData.value = getHabits()
    }

    private fun createHabitsMediator(): MediatorLiveData<List<Habit>> {
        val mediator = MediatorLiveData<List<Habit>>()
        mediator.addSource(repository.allHabits) {
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