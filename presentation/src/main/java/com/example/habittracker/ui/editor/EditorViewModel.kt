package com.example.habittracker.ui.editor

import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.repository.HabitRepository
import com.example.habittracker.middleware.Event
import kotlinx.coroutines.launch

class EditorViewModel(private val repository: HabitRepository) : ViewModel() {
    private val original: MutableLiveData<Habit> = MutableLiveData<Habit>()
        .apply {
            value = null
        }
    val cardExists: Boolean
        get() = original.value != null

    val editor: EditorFields = EditorFields()

    private val _onSaveButtonClick: MutableLiveData<Event<EditorFields>> =
        MutableLiveData<Event<EditorFields>>()
    val onSaveButtonClick: LiveData<Event<EditorFields>> = _onSaveButtonClick

    private val _onDeleteButtonClick: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    val onDeleteButtonClick: LiveData<Event<Int>> = _onDeleteButtonClick

    private fun updateOriginal(state: Habit) = viewModelScope.launch {
        original.value?.let { repository.update(it, state) }
    }

    private fun insertNew(state: Habit) = viewModelScope.launch {
        repository.insert(state)
    }

    private fun deleteOriginal() = viewModelScope.launch {
        original.value?.let {
            repository.delete(it)
        }
    }

    fun setCard(habit: Habit) {
        original.value = habit
        editor.fillFields(habit)
    }

    fun setEmptyCard() {
        original.value = null
        editor.clearFields()
    }

    private fun updateCard(editor: EditorFields) {
        val period = Periodicity(editor.repetitionsNumber.toInt(), editor.daysNumber.toInt())
        val state = Habit(
            editor.title,
            editor.description,
            period,
            editor.type,
            Priority.valueOf(editor.priority)
        )
        original.value.let {
            if (it != null) {
                updateOriginal(state)
            } else {
                insertNew(state)
            }
        }
    }

    fun onSave(view: View) {
        editor.let {
            if (it.isValid()) {
                updateCard(it)
                _onSaveButtonClick.value = Event(it)
            }
        }
    }

    fun onDelete(view: View) {
        deleteOriginal()
        _onDeleteButtonClick.value = Event(view.id)
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

class EditorViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}