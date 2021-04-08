package com.example.habittracker.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.habittracker.cards.BadCardsFragment
import com.example.habittracker.cards.GoodCardsFragment

class CardCollectionsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                GoodCardsFragment()
            }
            else -> {
                BadCardsFragment()
            }
        }
    }
}