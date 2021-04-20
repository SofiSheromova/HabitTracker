package com.example.habittracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.habittracker.R
import com.example.habittracker.ui.cards.*
import com.example.habittracker.databinding.FragmentHomeBinding
import com.example.habittracker.model.Card
import com.example.habittracker.ui.editor.EditorViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment :
    CardsAdapter.OnItemClickListener,
    Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var editorViewModel: EditorViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var cardCollectionsAdapter: CardCollectionsAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupCardCreateButton()

        homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        editorViewModel =
            ViewModelProvider(requireActivity()).get(EditorViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardCollectionsAdapter = CardCollectionsAdapter(this)

        viewPager = view.findViewById(R.id.cardCollectionsViewPager)
        viewPager.adapter = cardCollectionsAdapter

        val tabLayout = view.findViewById(R.id.cardCollectionsTabLayout) as TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.good_habits)
                else -> resources.getString(R.string.bad_habits)
            }
        }.attach()
    }

    override fun onItemClicked(card: Card) {
////        val action = HomeFragmentDirections.actionNavHomeToNavEditor(card)
////        Navigation.findNavController(binding.root).navigate(action)
        editorViewModel.setCard(card)
        Navigation.findNavController(binding.root).navigate(R.id.action_nav_home_to_nav_editor)
    }

    private fun setupCardCreateButton() {
        val icon = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_add_24,
            null
        )
        binding.cardCreateButton.setImageDrawable(icon)

        binding.cardCreateButton.setOnClickListener {
//            val action = HomeFragmentDirections.actionNavHomeToNavEditor(null)
//            Navigation.findNavController(binding.root).navigate(action)
            editorViewModel.setCard()
            Navigation.findNavController(binding.root).navigate(R.id.action_nav_home_to_nav_editor)
        }
    }
}

