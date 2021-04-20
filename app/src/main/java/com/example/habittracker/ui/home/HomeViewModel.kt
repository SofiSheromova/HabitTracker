package com.example.habittracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.model.Card
import com.example.habittracker.model.Periodicity
import com.example.habittracker.model.Type

class HomeViewModel : ViewModel() {
    private val _habitsLiveData: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>().apply {
        value = Card.getAll()
    }

    val habitsLiveData: LiveData<List<Card>> = _habitsLiveData

    fun refreshValue() {
        _habitsLiveData.value = Card.getAll()
    }
}