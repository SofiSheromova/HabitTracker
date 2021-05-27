package com.example.habittracker.ui.editor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.habittracker.HabitTrackerApplication
import javax.inject.Inject

class SimpleViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    @Inject
    lateinit var appName: String

    init {
        (application as HabitTrackerApplication)
            .viewModelSubComponent
            .inject(this)
        Log.d("TAG-HELP", appName)
    }
}