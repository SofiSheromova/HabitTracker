package com.example.habittracker.ui.home

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.model.DisplayOptions

class DisplayOptionsViewModel : ViewModel() {
    private val _checkedButtonLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    val checkedButtonLiveData: LiveData<Int> = _checkedButtonLiveData

    private val _optionsLiveData: MutableLiveData<DisplayOptions> =
        MutableLiveData<DisplayOptions>()
    val optionsLiveData: LiveData<DisplayOptions> = _optionsLiveData

    private fun optionsRefresh() {
        // на самом деле обсерверам не нужны эти данные, а просто факт того, что данные изменились
        _optionsLiveData.value = DisplayOptions()
    }

    fun updateSearchBarData(s: Editable) {
        DisplayOptions.searchBarData = s.toString()
        this.optionsRefresh()
    }

    fun sortByTitle(id: Int, reverse: Boolean = false) {
        DisplayOptions.sortByTitle(reverse)
        this.optionsRefresh()
        _checkedButtonLiveData.value = id
    }

    interface Factory : ViewModelProvider.Factory
}

