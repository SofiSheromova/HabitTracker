package com.example.habittracker.ui.editor

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.example.habittracker.R
import com.example.habittracker.BR
import com.example.habittracker.model.Habit
import com.example.habittracker.model.Type


class EditorFields : BaseObservable() {
    var title: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    var description: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    var type: Type = Type.GOOD
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    fun setGoodType() {
        type = Type.GOOD
    }

    fun setBadType() {
        type = Type.BAD
    }

    var priority: Int = 1
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    private var _repetitionsNumber: Int? = 1
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    var repetitionsNumber: String
        get() = _repetitionsNumber.toString()
        set(value) {
            _repetitionsNumber = value.toIntOrNull()
        }

    private var _daysNumber: Int? = 1
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }

    var daysNumber: String
        get() = _daysNumber.toString()
        set(value) {
            _daysNumber = value.toIntOrNull()
        }

    fun clearFields() {
        fillFields(Habit())
    }

    fun fillFields(state: Habit) {
        title = state.title
        description = state.description
        type = state.type
        priority = state.priority
        _repetitionsNumber = state.periodicity.repetitionsNumber
        _daysNumber = state.periodicity.daysNumber
    }

    var titleError: ObservableField<Int> = ObservableField()
    var descriptionError: ObservableField<Int> = ObservableField()
    var repetitionsNumberError: ObservableField<Int> = ObservableField()
    val daysNumberError: ObservableField<Int> = ObservableField()

    @Bindable
    fun isValid(): Boolean {
        return isTitleValid(false) &&
                isDescriptionValid(false) &&
                isRepetitionsNumberValid(false) &&
                isDaysNumberValid(false)
    }

    fun isTitleValid(setMessage: Boolean): Boolean {
        if (title.length in TITLE_MIN_LENGTH..TITLE_MAX_LENGTH) {
            titleError.set(null)
            return true
        }
        if (setMessage && title.length < TITLE_MIN_LENGTH)
            titleError.set(R.string.error_too_short)
        if (setMessage && title.length > TITLE_MAX_LENGTH)
            titleError.set(R.string.error_too_long)
        return false
    }

    fun isDescriptionValid(setMessage: Boolean): Boolean {
        if (description.length <= DESCRIPTION_MAX_LENGTH) {
            titleError.set(null)
            return true
        }
        if (setMessage)
            titleError.set(R.string.error_too_long)
        return false
    }

    fun isRepetitionsNumberValid(setMessage: Boolean): Boolean {
        if (_repetitionsNumber != null && _repetitionsNumber!! > 0) {
            repetitionsNumberError.set(null)
            return true
        }
        if (setMessage)
            repetitionsNumberError.set(R.string.invalid_value)
        return false
    }

    fun isDaysNumberValid(setMessage: Boolean): Boolean {
        if (_daysNumber != null && _daysNumber!! > 0) {
            daysNumberError.set(null)
            return true
        }
        if (setMessage)
            daysNumberError.set(R.string.invalid_value)
        return false
    }

    companion object {
        private const val TITLE_MIN_LENGTH: Int = 5
        private const val TITLE_MAX_LENGTH: Int = 50

        private const val DESCRIPTION_MAX_LENGTH: Int = 140
    }
}