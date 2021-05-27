package com.example.habittracker.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.InsertHabitUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentEditorBinding
import javax.inject.Inject

class EditorFragment : Fragment() {
    private lateinit var editorViewModel: EditorViewModel
    private lateinit var binding: FragmentEditorBinding

    @Inject
    lateinit var insertHabitUseCase: InsertHabitUseCase

    @Inject
    lateinit var updateHabitUseCase: UpdateHabitUseCase

    @Inject
    lateinit var deleteHabitUseCase: DeleteHabitUseCase

    @Inject
    lateinit var simpleViewModel: SimpleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (requireActivity().application as HabitTrackerApplication).appComponent
        appComponent.fragmentSubComponentBuilder().with(this).build()
        appComponent.inject(this)

        editorViewModel = ViewModelProvider(
            requireActivity(),
            EditorViewModelFactory(insertHabitUseCase, updateHabitUseCase, deleteHabitUseCase)
        )
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
            it?.getContentIfNotHandled()?.let {
                Navigation.findNavController(binding.root).popBackStack()
            }
        })

        editorViewModel.onDeleteButtonClick.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let {
                Navigation.findNavController(binding.root).popBackStack()
            }
        })
    }
}
