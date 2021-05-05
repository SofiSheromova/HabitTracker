package com.example.habittracker.ui.editor

import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.habittracker.model.Card
import com.example.habittracker.model.CardRepository
import com.example.habittracker.model.Periodicity
import kotlinx.coroutines.launch

class EditorViewModel(private val repository: CardRepository) : ViewModel() {
    private val original: MutableLiveData<Card> = MutableLiveData<Card>()
        .apply {
            value = null
        }
    val cardExists: Boolean
        get() = original.value != null

    val editor: EditorFields = EditorFields()
    var isCardSaving = false

    private val _onSaveButtonClick: MutableLiveData<EditorFields> = MutableLiveData<EditorFields>()
    val onSaveButtonClick: LiveData<EditorFields> = _onSaveButtonClick

    private val _onDeleteButtonClick: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val onDeleteButtonClick: LiveData<Boolean> = _onDeleteButtonClick

    private fun updateOriginal(state: Card) = viewModelScope.launch {
        original.value?.let { repository.update(it, state) }
    }

    private fun insertNew(state: Card) = viewModelScope.launch {
        repository.insertAll(state)
    }

    private fun deleteOriginal() = viewModelScope.launch {
        original.value?.let {
            repository.delete(it)
        }
    }

    fun setCard(card: Card) {
        original.value = card
        editor.fillFields(card)
    }

    fun setEmptyCard() {
        original.value = null
        editor.clearFields()
    }

    private fun updateCard(editor: EditorFields) {
        val period = Periodicity(editor.repetitionsNumber.toInt(), editor.daysNumber.toInt())
        val state = Card(editor.title, editor.description, period, editor.type, editor.priority)
        original.value.let {
            if (it != null) {
                updateOriginal(state)
            } else {
                insertNew(state)
            }
        }
    }

    fun onSave() {
        editor.let {
            if (it.isValid()) {
                updateCard(it)
                isCardSaving = true
                _onSaveButtonClick.value = it
            }
        }
    }

    fun onDelete() {
        deleteOriginal()
        _onDeleteButtonClick.value = true
        _onDeleteButtonClick.value = false
    }

    val onFocusTitle: OnFocusChangeListener = OnFocusChangeListener { view, focused ->
        val et = view as EditText
        if (et.text.isNotEmpty() && !focused) {
            editor.isTitleValid(true)
        }
    }

    val onFocusDescription: OnFocusChangeListener = OnFocusChangeListener { view, focused ->
        val et = view as EditText
        if (et.text.isNotEmpty() && !focused) {
            editor.isDescriptionValid(true)
        }
    }

    val onFocusRepetitionsNumber: OnFocusChangeListener = OnFocusChangeListener { view, focused ->
        val et = view as EditText
        if (et.text.isNotEmpty() && !focused) {
            editor.isRepetitionsNumberValid(true)
        }
    }

    val onFocusDaysNumber: OnFocusChangeListener = OnFocusChangeListener { view, focused ->
        val et = view as EditText
        if (et.text.isNotEmpty() && !focused) {
            editor.isDaysNumberValid(true)
        }
    }

    companion object {
        @BindingAdapter("error")
        @JvmStatic
        fun setError(editText: EditText, strOrResId: Int?) {
            if (strOrResId is Int) {
                editText.error = editText.context.getString((strOrResId as Int?)!!)
            } else {
                editText.error = strOrResId as String?
            }
        }

        @BindingAdapter("onFocus")
        @JvmStatic
        fun bindFocusChange(editText: EditText, onFocusChangeListener: OnFocusChangeListener?) {
            if (editText.onFocusChangeListener == null) {
                editText.onFocusChangeListener = onFocusChangeListener
            }
        }
    }
}

class EditorViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}