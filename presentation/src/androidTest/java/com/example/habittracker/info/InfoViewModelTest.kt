package com.example.habittracker.info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.habittracker.ui.info.InfoViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class InfoViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val infoViewModel = InfoViewModel()

    @Test
    fun checkText() {
        assertEquals("This is info Fragment", infoViewModel.text.value)
    }
}