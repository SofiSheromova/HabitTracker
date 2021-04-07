package com.example.habittracker.cardCollections

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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