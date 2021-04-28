package com.example.habittracker.ui.cards

import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.*
import com.example.habittracker.model.Card
import com.example.habittracker.model.CardRepository

class CardsViewModel(private val repository: CardRepository) : ViewModel() {
    val searchBarLiveData: MutableLiveData<String> = MutableLiveData<String>()
    private val _habitsLiveData: MediatorLiveData<List<Card>> = createHabitsMediator()
    val habitsLiveData: LiveData<List<Card>> = _habitsLiveData

    private var _sortedFunction: ((List<Card>) -> List<Card>)? = null

    private val _checkedSortButtonLiveData: MutableLiveData<ImageButton> =
        MutableLiveData<ImageButton>()
    val checkedSortButtonLiveData: LiveData<ImageButton> = _checkedSortButtonLiveData

    private fun getHabitsValue(): List<Card> {
        var newValue = repository.allCards.value ?: listOf()

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

    private fun createHabitsMediator(): MediatorLiveData<List<Card>> {
        val mediator = MediatorLiveData<List<Card>>()
        mediator.addSource(repository.allCards) {
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

class CardsViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
