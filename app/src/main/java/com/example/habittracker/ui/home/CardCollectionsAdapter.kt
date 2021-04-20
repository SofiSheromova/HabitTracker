package com.example.habittracker.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.habittracker.ui.cards.CardsFragment

class CardCollectionsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = CardsFragment()
        fragment.arguments = Bundle().apply {
            when (position) {
                0 -> {
                    putString(CardsFragment.FILTER_NAME, CardsFragment.GOOD_TYPE)
                }
                else -> {
                    putString(CardsFragment.FILTER_NAME, CardsFragment.BAD_TYPE)
                }
            }
        }
        return fragment
    }
}