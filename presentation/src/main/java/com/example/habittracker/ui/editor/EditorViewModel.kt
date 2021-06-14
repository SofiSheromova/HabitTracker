package com.example.habittracker.ui.editor

import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.InsertHabitUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.util.Event
import kotlinx.coroutines.launch
import kotlin.math.min

class EditorViewModel constructor(
    private val insertHabitUseCase: InsertHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val defaultCardColor: Int = -1
) : ViewModel() {

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

    private fun updateOriginal(state: Habit) = viewModelScope.launch {
        original.value?.let { updateHabitUseCase.update(it, state) }
    }

    private fun insertNew(state: Habit) = viewModelScope.launch {
        insertHabitUseCase.insert(state)
    }

    private fun deleteOriginal() = viewModelScope.launch {
        original.value?.let { deleteHabitUseCase.delete(it) }
    }

    fun setCard(habit: Habit) {
        original.value = habit
        editor.fillFields(habit)
    }

    fun setEmptyCard() {
        original.value = null
        val habit = Habit()

        habit.color = defaultCardColor
        editor.fillFields(habit)
    }

    private fun updateCard(editor: EditorFields) {
        val period = Periodicity(editor.repetitionsNumber.toInt(), editor.daysNumber.toInt())
        val state = Habit(
            editor.title,
            editor.description,
            period,
            editor.type,
            editor.priority,
            editor.color
        )
        original.value.let {
            if (it != null)
                updateOriginal(state)
            else
                insertNew(state)
        }
    }

    fun onSave() {
        editor.let {
            if (it.isValid()) {
                updateCard(it)
                _onSaveButtonClick.value = Event(it)
            }
        }
    }

    fun onDelete() {
        deleteOriginal()
    }

    val onPrioritySelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            editor.priority = Priority.valueOf(min(Priority.HIGH.value, position))
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
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

        @BindingAdapter("app:tint")
        @JvmStatic
        fun setTint(view: ImageView, color: Int) {
            view.setColorFilter(color)
        }
    }

    interface Factory : ViewModelProvider.Factory
}

