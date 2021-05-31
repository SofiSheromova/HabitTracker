package com.example.habittracker.ui.editor

import android.app.Application
import android.graphics.Color
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EditorViewModel @Inject constructor(
    private val insertHabitUseCase: InsertHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
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

    private val _onDeleteButtonClick: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    val onDeleteButtonClick: LiveData<Event<Int>> = _onDeleteButtonClick

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
        editor.clearFields()
    }

    private fun updateCard(editor: EditorFields) {
        val period = Periodicity(editor.repetitionsNumber.toInt(), editor.daysNumber.toInt())
        val state = Habit(
            editor.title,
            editor.description,
            period,
            editor.type,
            Priority.valueOf(editor.priority),
            generateColor()
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

    private fun generateColor(): Int {
        val color = String.format(
            "#%06X", 0xFFFFFF and Color.argb(
                100,
                Random.nextInt(180, 240),
                Random.nextInt(180, 240),
                Random.nextInt(180, 240)
            )
        )
        return Color.parseColor(color)
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

interface EditorViewModelFactory : ViewModelProvider.Factory
