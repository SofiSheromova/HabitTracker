package com.example.habittracker.ui.cards

import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.*
import com.example.habittracker.model.Habit
import com.example.habittracker.model.HabitRepository

class CardsViewModel(private val repository: HabitRepository) : ViewModel() {
    val searchBarLiveData: MutableLiveData<String> = MutableLiveData<String>()
    private val _habitsLiveData: MediatorLiveData<List<Habit>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Habit>> = _habitsLiveData

    private var _sortedFunction: ((List<Habit>) -> List<Habit>)? = null

    private val _checkedSortButtonLiveData: MutableLiveData<ImageButton> =
        MutableLiveData<ImageButton>()
    val checkedSortButtonLiveData: LiveData<ImageButton> = _checkedSortButtonLiveData

    private fun getHabitsValue(): List<Habit> {
        var newValue = repository.allHabits.value ?: listOf()

        _sortedFunction?.let {
            newValue = it(newValue)
        }

        searchBarLiveData.value?.let { request ->
            if (request.isNotEmpty()) {
                newValue = newValue.filter { it.title.contains(request) }
            }
        }

        return newValue
    }

    fun refreshHabitsValue() {
        _habitsLiveData.value = getHabitsValue()
    }

    private fun createHabitsMediator(): MediatorLiveData<List<Habit>> {
        val mediator = MediatorLiveData<List<Habit>>()
        mediator.addSource(repository.allHabits) {
            mediator.value = getHabitsValue()
        }
        return mediator
    }

    fun sortByTitle(reverse: Boolean = false) {
        _sortedFunction = if (reverse) {
            { cards -> cards.sortedBy { it.title }.asReversed() }
        } else {
            { cards -> cards.sortedBy { it.title } }
        }
        refreshHabitsValue()
    }

    fun sortByTitle(view: View) {
        sortByTitle()
        _checkedSortButtonLiveData.value = view as ImageButton
    }

    fun reverseSortByTitle(view: View) {
        sortByTitle(true)
        _checkedSortButtonLiveData.value = view as ImageButton
    }
}

class CardsViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
