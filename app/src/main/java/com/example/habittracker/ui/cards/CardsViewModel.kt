package com.example.habittracker.ui.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.model.Card

class CardsViewModel : ViewModel() {
    private val _habitsLiveData: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>().apply {
        value = Card.getAll()
    }
    val habitsLiveData: LiveData<List<Card>> = _habitsLiveData
    val searchBarLiveData: MutableLiveData<String> = MutableLiveData<String>()

    private var _sortedFunction: ((List<Card>) -> List<Card>)? = null

    fun refreshValue() {
        var newValue = Card.getAll()

        _sortedFunction?.let {
            newValue = it(newValue)
        }

        searchBarLiveData.value?.let { request ->
            if (request.isNotEmpty()) {
                newValue = newValue.filter { it.title.contains(request) }
            }
        }

        _habitsLiveData.value = newValue
    }

    fun sortByTitle(reverse: Boolean = false) {
        _sortedFunction = if (reverse) {
            { cards -> cards.sortedBy { it.title }.asReversed() }
        } else {
            { cards -> cards.sortedBy { it.title } }
        }
        refreshValue()
    }
}
