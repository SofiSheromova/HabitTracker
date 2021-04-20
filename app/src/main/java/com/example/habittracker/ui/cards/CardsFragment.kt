package com.example.habittracker.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.databinding.FragmentCardsBinding
import com.example.habittracker.model.Card
import com.example.habittracker.model.Type
import com.example.habittracker.ui.home.HomeViewModel

class CardsFragment : Fragment() {
    private lateinit var binding: FragmentCardsBinding
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var adapter: CardsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        val view = binding.root

        homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        var filter: (Card) -> Boolean = { true }
        arguments?.takeIf { it.containsKey(FILTER_NAME) }?.apply {
            filter = FILTERS[getString(FILTER_NAME)] ?: filter
        }

        val cards = homeViewModel.habitsLiveData.value
        val filteredCards: MutableList<Card> =
            cards?.filter(filter)?.toMutableList() ?: mutableListOf()

        val onItemClickListener = parentFragment as CardsAdapter.OnItemClickListener

        adapter = CardsAdapter(filteredCards, onItemClickListener)

        binding.cardsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.cardsRecycler.adapter = adapter
        return view
    }

    companion object {
        const val FILTER_NAME = "object"
        const val GOOD_TYPE = "GOOD_TYPE"
        const val BAD_TYPE = "BAD_TYPE"
        private val FILTERS = mapOf<String, (Card) -> Boolean>(
            GOOD_TYPE to { card -> card.type == Type.GOOD },
            BAD_TYPE to { card -> card.type == Type.BAD }
        )
    }
}