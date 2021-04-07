package com.example.habittracker.cardCollections

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cards
import com.example.habittracker.R
import com.example.habittracker.cards.*
import com.example.habittracker.model.Card
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CardCollectionsFragment : Fragment(), CardsAdapter.OnItemClickListener,
    HasCardsAdapterManager, OnFragmentSendDataListener {
    private lateinit var cardCollectionsAdapter: CardCollectionsAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentSendDataListener: OnFragmentSendDataListener
    override lateinit var adapterManager: CardsAdapterManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement the interface " +
                        "OnFragmentSendDataListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterManager = CardsAdapterManager(cards, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_collections, container, false)
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
        fragmentSendDataListener.onSendCard(card)
    }

    override fun onSendCard(selectedItem: Card) {
        fragmentSendDataListener.onSendCard(selectedItem)
    }
}
