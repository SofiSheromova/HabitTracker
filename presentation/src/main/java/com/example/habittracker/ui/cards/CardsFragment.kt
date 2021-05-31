package com.example.habittracker.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.model.Habit
import com.example.domain.model.Type
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentCardsBinding
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.ui.editor.EditorViewModel
import com.example.habittracker.ui.editor.EditorViewModelFactory
import com.example.habittracker.ui.home.DisplayOptionsViewModel
import com.example.habittracker.ui.home.DisplayOptionsViewModelFactory
import javax.inject.Inject

class CardsFragment : Fragment(), CardsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCardsBinding

    private lateinit var cardsViewModel: CardsViewModel

    private lateinit var editorViewModel: EditorViewModel

    private lateinit var displayOptionsViewModel: DisplayOptionsViewModel

    private lateinit var adapter: CardsAdapter

    @Inject
    lateinit var cardsViewModelFactory: CardsViewModelFactory

    @Inject
    lateinit var editorViewModelFactory: EditorViewModelFactory

    @Inject
    lateinit var displayOptionsViewModelFactory: DisplayOptionsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity().application as HabitTrackerApplication).appComponent
            .inject(this)

        editorViewModel = ViewModelProvider(requireActivity(), editorViewModelFactory)
            .get(EditorViewModel::class.java)

        displayOptionsViewModel = ViewModelProvider(
            requireActivity(), displayOptionsViewModelFactory
        )
            .get(DisplayOptionsViewModel::class.java)

        val displayOptions = getDisplayOptions(arguments)

        cardsViewModel = ViewModelProvider(this, cardsViewModelFactory)
            .get(CardsViewModel::class.java)

        adapter = CardsAdapter(cardsViewModel.habitsLiveData, this, requireContext())

        displayOptionsViewModel.optionsLiveData.observe(this, {
            cardsViewModel.refreshHabits()
        })
        cardsViewModel.habitsLiveData.observe(this) {
            adapter.notifyDataSetChanged()
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardsViewModel.refreshLiveData.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled().let { binding.swiperefresh.isRefreshing = it ?: false }
        })
        cardsViewModel.networkError.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                if (it)
                    Toast.makeText(
                        requireContext().applicationContext,
                        resources.getString(R.string.failed_update),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        })
        cardsViewModel.habitFulfillmentReport.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext().applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.swiperefresh.setOnRefreshListener {
            cardsViewModel.refresh()
        }
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

    override fun onDoneButtonClicked(habit: Habit) {
        cardsViewModel.markDone(habit)
    }
}