package com.example.habittracker.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import cards
import com.example.habittracker.R
import com.example.habittracker.cardCollections.CardCollectionsAdapter
import com.example.habittracker.cards.CardsAdapter
import com.example.habittracker.cards.CardsAdapterManager
import com.example.habittracker.cards.HasCardsAdapterManager
import com.example.habittracker.databinding.FragmentCardCollectionsBinding
import com.example.habittracker.model.Card
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment :
    Fragment(),
    CardsAdapter.OnItemClickListener,
//    OnFragmentSendDataListener,
    HasCardsAdapterManager {

    private lateinit var binding: FragmentCardCollectionsBinding
    private lateinit var cardCollectionsAdapter: CardCollectionsAdapter
    private lateinit var viewPager: ViewPager2

    //    private lateinit var fragmentSendDataListener: OnFragmentSendDataListener
    override lateinit var adapterManager: CardsAdapterManager

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            fragmentSendDataListener = context as OnFragmentSendDataListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                "$context must implement the interface " +
//                        "OnFragmentSendDataListener"
//            )
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterManager = CardsAdapterManager(cards, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardCollectionsBinding.inflate(inflater, container, false)
        setupCardCreateButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardCollectionsAdapter = CardCollectionsAdapter(this)
        viewPager = view.findViewById(R.id.cardCollectionsViewPager)
        viewPager.adapter = cardCollectionsAdapter

        val tabLayout = view.findViewById(R.id.cardCollectionsTabLayout) as TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "collection ${(position + 1)}"
        }.attach()

    }

    override fun onItemClicked(card: Card) {
        val action = HomeFragmentDirections.actionNavHomeToNavEditor(card)
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun setupCardCreateButton() {
        val icon = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_add_24,
            null
        )
        binding.cardCreateButton.setImageDrawable(icon)

        binding.cardCreateButton.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToNavEditor(null)
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

//    override fun onSendCard(selectedItem: Card) {
//        fragmentSendDataListener.onSendCard(selectedItem)
//    }
}