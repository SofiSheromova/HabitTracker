package com.example.habittracker.ui.editor

import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.model.Card
import com.example.habittracker.model.Periodicity
import com.example.habittracker.model.Type

class EditorViewModel : ViewModel() {
    private val _original: MutableLiveData<Card> = MutableLiveData<Card>()
        .apply {
            value = null
        }

    val editor: EditorFields = EditorFields()
    var isSaving = false

    fun setCard(card: Card) {
        _original.value = card
        editor.fillFields(card)
    }

    fun setEmptyCard() {
        _original.value = null
        editor.clearFields()
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

    private val _buttonClick: MutableLiveData<EditorFields> = MutableLiveData<EditorFields>()
    val buttonClick: LiveData<EditorFields> = _buttonClick

    private fun updateCard(editor: EditorFields) {
        val period = Periodicity(editor.repetitionsNumber.toInt(), editor.daysNumber.toInt())
        val state = Card(editor.title, editor.description, period, editor.type, editor.priority)
        _original.value.let {
            if (it != null) {
                Card.update(it.id, state)
            } else {
                Card.insertAll(state)
            }
        }
    }

    fun onButtonClick() {
        editor.let {
            if (it.isValid()) {
                updateCard(it)
                isSaving = true
                _buttonClick.value = it
            }
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