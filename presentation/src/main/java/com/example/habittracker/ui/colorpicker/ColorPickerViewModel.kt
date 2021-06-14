package com.example.habittracker.ui.colorpicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.util.Event

class ColorPickerViewModel : ViewModel() {
    private val colorMutableLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val colorLiveData: LiveData<Event<Int>> = colorMutableLiveData

    fun setColor(color: Int) {
        colorMutableLiveData.value = Event(color)
    }
}