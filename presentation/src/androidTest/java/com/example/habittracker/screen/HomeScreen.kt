package com.example.habittracker.screen

import com.example.habittracker.R
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton

class HomeScreen : Screen<HomeScreen>() {
    val cardCreateButton = KButton { withId(R.id.card_create_button) }
}