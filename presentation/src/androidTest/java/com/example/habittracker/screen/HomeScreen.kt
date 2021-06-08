package com.example.habittracker.screen

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.example.habittracker.R

class HomeScreen : Screen<HomeScreen>() {
    val cardCreateButton = KButton { withId(R.id.card_create_button) }
}