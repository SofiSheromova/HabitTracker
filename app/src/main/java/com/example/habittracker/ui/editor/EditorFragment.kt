package com.example.habittracker.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentEditorBinding
import com.example.habittracker.ui.cards.CardsViewModel
import com.example.habittracker.ui.cards.CardsViewModelFactory

class EditorFragment : Fragment() {
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var editorViewModel: EditorViewModel
    private lateinit var binding: FragmentEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (requireActivity().application as HabitTrackerApplication).repository

        cardsViewModel = ViewModelProvider(requireActivity(), CardsViewModelFactory(repository))
            .get(CardsViewModel::class.java)
        editorViewModel = ViewModelProvider(requireActivity(), EditorViewModelFactory(repository))
            .get(EditorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_editor, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = editorViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editorViewModel.onSaveButtonClick.observe(viewLifecycleOwner, {
            if (editorViewModel.isCardSaving) {
                editorViewModel.isCardSaving = false

                Navigation.findNavController(binding.root).popBackStack()
            }
        })

        editorViewModel.onDeleteButtonClick.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Navigation.findNavController(binding.root).popBackStack()
                }
            }
        })
    }
}
