package com.example.habittracker.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentCardsBinding
import com.example.habittracker.model.Habit
import com.example.habittracker.model.Type
import com.example.habittracker.ui.editor.EditorViewModel
import com.example.habittracker.ui.editor.EditorViewModelFactory
import com.example.habittracker.ui.home.DisplayOptions
import com.example.habittracker.ui.home.DisplayOptionsViewModel

class CardsFragment : Fragment(), CardsAdapter.OnItemClickListener {
    private lateinit var displayOptionsViewModel: DisplayOptionsViewModel
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var editorViewModel: EditorViewModel

    private lateinit var binding: FragmentCardsBinding

    private lateinit var adapter: CardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (requireActivity().application as HabitTrackerApplication).repository

        val displayOptions = getDisplayOptions(arguments)

        displayOptionsViewModel = ViewModelProvider(requireActivity())
            .get(DisplayOptionsViewModel::class.java)
        cardsViewModel = ViewModelProvider(this, CardsViewModelFactory(repository, displayOptions))
            .get(CardsViewModel::class.java)
        editorViewModel = ViewModelProvider(requireActivity(), EditorViewModelFactory(repository))
            .get(EditorViewModel::class.java)

        adapter = CardsAdapter(cardsViewModel.habitsLiveData, this, requireContext())

        displayOptionsViewModel.optionsLiveData.observe(this, {
            cardsViewModel.refreshHabits()
        })
        cardsViewModel.habitsLiveData.observe(this, {
            adapter.notifyDataSetChanged()
        })
    }

    private fun getDisplayOptions(args: Bundle?): DisplayOptions {
        var typeFilter: ((Habit) -> Boolean)? = null
        args?.takeIf { it.containsKey(FILTER_NAME) }?.apply {
            typeFilter = FILTERS[getString(FILTER_NAME)]
        }

        return typeFilter?.let { DisplayOptions(it) } ?: DisplayOptions()
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
        private val FILTERS = mapOf<String, (Habit) -> Boolean>(
            GOOD_TYPE to { card -> card.type == Type.GOOD },
            BAD_TYPE to { card -> card.type == Type.BAD }
        )
    }

    override fun onItemClicked(habit: Habit) {
        editorViewModel.setCard(habit)
        Navigation.findNavController(binding.root).navigate(R.id.action_nav_home_to_nav_editor)
    }
}