package com.example.habittracker.di.component

import com.example.habittracker.MainActivity
import com.example.habittracker.di.module.AppModule
import com.example.habittracker.ui.cards.CardsFragment
import com.example.habittracker.ui.editor.EditorFragment
import com.example.habittracker.ui.home.HomeFragment
import com.example.habittracker.ui.info.InfoFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // Activity
    fun inject(mainActivity: MainActivity)

    // Fragments
    fun inject(infoFragment: InfoFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(cardsFragment: CardsFragment)
    fun inject(editorFragment: EditorFragment)
}