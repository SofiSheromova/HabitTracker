package com.example.habittracker.cards

import android.os.Bundle

class BadCardsFragment : BaseCardsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = adapterManager.createFilterAdapter { it.type == Type.BAD }
    }
}