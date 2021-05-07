package com.example.habittracker.ui.home

import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DisplayOptionsViewModel : ViewModel() {
    val searchBarLiveData: MutableLiveData<String> = MutableLiveData<String>()

    private val _checkedButtonLiveData: MutableLiveData<ImageButton> =
        MutableLiveData<ImageButton>()
    val checkedButtonLiveData: LiveData<ImageButton> = _checkedButtonLiveData

    private val _optionsLiveData: MutableLiveData<DisplayOptions> =
        MutableLiveData<DisplayOptions>()
    val optionsLiveData: LiveData<DisplayOptions> = _optionsLiveData

    private fun optionsRefresh() {
        // на самом деле обсерверам не нужны эти данные, а просто факт того, что данные изменились
        _optionsLiveData.value = DisplayOptions()
    }

    fun updateSearchBarData() {
        DisplayOptions.searchBarData = searchBarLiveData.value
        this.optionsRefresh()
    }

    fun sortByTitle(view: View) {
        setSortingByTitle(view)
    }

    fun reverseSortByTitle(view: View) {
        setSortingByTitle(view, true)
    }

    private fun setSortingByTitle(view: View, reverse: Boolean = false) {
        DisplayOptions.sortByTitle(reverse)
        this.optionsRefresh()
        _checkedButtonLiveData.value = view as ImageButton
    }
}