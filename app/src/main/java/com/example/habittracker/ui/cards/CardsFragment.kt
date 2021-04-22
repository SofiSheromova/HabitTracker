package com.example.habittracker.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentCardsBinding
import com.example.habittracker.model.Card
import com.example.habittracker.model.Type
import com.example.habittracker.ui.editor.EditorViewModel

class CardsFragment : Fragment(), CardsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCardsBinding

    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var editorViewModel: EditorViewModel

    private lateinit var adapter: CardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardsViewModel = ViewModelProvider(requireActivity())
            .get(CardsViewModel::class.java)
        editorViewModel = ViewModelProvider(requireActivity())
            .get(EditorViewModel::class.java)

        var filter: ((Card) -> Boolean)? = null
        arguments?.takeIf { it.containsKey(FILTER_NAME) }?.apply {
            filter = FILTERS[getString(FILTER_NAME)]
        }

        adapter = CardsAdapter(cardsViewModel.habitsLiveData, this, filter)

        cardsViewModel.habitsLiveData.observe(this, {
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(inflater, container, false)

        binding.cardsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.cardsRecycler.adapter = adapter

        return binding.root
    }

    companion object {
        const val FILTER_NAME = "FILTER_NAME"
        const val GOOD_TYPE = "GOOD_TYPE"
        const val BAD_TYPE = "BAD_TYPE"
        private val FILTERS = mapOf<String, (Card) -> Boolean>(
            GOOD_TYPE to { card -> card.type == Type.GOOD },
            BAD_TYPE to { card -> card.type == Type.BAD }
        )
    }

    override fun onItemClicked(card: Card) {
        editorViewModel.setCard(card)
        Navigation.findNavController(binding.root).navigate(R.id.action_nav_home_to_nav_editor)
    }
}