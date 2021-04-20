package com.example.habittracker.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.model.Card
import com.example.habittracker.model.Periodicity
import com.example.habittracker.model.Type

class EditorViewModel : ViewModel() {
    private val _original: MutableLiveData<Card> = MutableLiveData<Card>()
        .apply {
            value = Card()
        }

    private val _stateLiveData: MutableLiveData<Card> = MutableLiveData<Card>()
        .apply {
            value = Card()
        }

    val cardLiveData: LiveData<Card> = _stateLiveData

    fun setCard() {
        _original.value = null
        _stateLiveData.value = Card()
    }

    fun setCard(card: Card) {
        _original.value = card
        _stateLiveData.value = card.copy()
    }

    fun updateCard() {
        _original.value.let { original ->
            if (original != null) {
                _stateLiveData.value?.let { state -> Card.update(original.id, state) }
            } else {
                _stateLiveData.value?.let { state -> Card.insertAll(state) }
            }
        }

    }

    fun setTitle(value: String) {
        _stateLiveData.value?.title = value
    }

    fun setDescription(value: String) {
        _stateLiveData.value?.description = value
    }

    fun setType(value: Int) {
        _stateLiveData.value?.type = Type.valueOf(value)
    }

    fun setPriority(value: Int) {
        _stateLiveData.value?.priority = value
    }

    fun setRepetitionsNumber(value: Int) {
        _stateLiveData.value?.let {
            it.periodicity = Periodicity(value, it.periodicity.daysNumber)
        }
    }

    fun setDaysNumber(value: Int) {
        _stateLiveData.value?.let {
            it.periodicity = Periodicity(it.periodicity.repetitionsNumber, value)
        }
    }
}