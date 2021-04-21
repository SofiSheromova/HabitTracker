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

    fun refreshValue() {
        _habitsLiveData.value = Card.getAll()
    }
}