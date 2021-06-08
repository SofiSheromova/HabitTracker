package com.example.habittracker.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentHomeBinding
import com.example.habittracker.ui.editor.EditorViewModel
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    lateinit var editorViewModel: EditorViewModel

    lateinit var displayOptionsViewModel: DisplayOptionsViewModel

    @Inject
    lateinit var editorViewModelFactory: EditorViewModel.Factory

    @Inject
    lateinit var displayOptionsViewModelFactory: DisplayOptionsViewModel.Factory

    private lateinit var cardCollectionsAdapter: CardCollectionsAdapter
    private lateinit var viewPager: ViewPager2

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = displayOptionsViewModel
        setupCardCreateButton()
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cardCollectionsAdapter = CardCollectionsAdapter(this)

        viewPager = binding.cardCollectionsViewPager
        viewPager.adapter = cardCollectionsAdapter

        val tabLayout = binding.cardCollectionsTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.all_habits)
                1 -> resources.getString(R.string.good_habits)
                else -> resources.getString(R.string.bad_habits)
            }
        }.attach()

        val sortButtonsByTitle = listOf(binding.titleSortButton, binding.titleReverseSortButton)
        setDefaultButtonsState(sortButtonsByTitle)

        displayOptionsViewModel.checkedButtonLiveData.observe(viewLifecycleOwner, { id ->
            setDefaultButtonsState(sortButtonsByTitle)
            if (id == null) return@observe
            sortButtonsByTitle.firstOrNull { it.id == id }?.let { setCheckedButtonState(it) }
        })
    }

    private fun setDefaultButtonsState(buttons: List<ImageButton>) {
        buttons.forEach { setDefaultButtonState(it) }
    }

    private fun setDefaultButtonState(button: ImageButton) {
        button.backgroundTintList = resources.getColorStateList(R.color.gray_200, null)
        button.setColorFilter(ContextCompat.getColor(button.context, R.color.textColor))
    }

    private fun setCheckedButtonState(button: ImageButton) {
        button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary, null)
        button.setColorFilter(ContextCompat.getColor(button.context, R.color.white))
    }

    private fun setupCardCreateButton() {
        binding.cardCreateButton.setOnClickListener {
            editorViewModel.setEmptyCard()
            Navigation.findNavController(binding.root).navigate(R.id.action_nav_home_to_nav_editor)
        }
    }
}
