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
import com.example.habittracker.ui.colorpicker.ColorPickerViewModel
import javax.inject.Inject

class EditorFragment : Fragment() {
    private lateinit var binding: FragmentEditorBinding

    private lateinit var editorViewModel: EditorViewModel
    private lateinit var colorPickerViewModel: ColorPickerViewModel

    @Inject
    lateinit var editorViewModelFactory: EditorViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity().application as HabitTrackerApplication).appComponent
            .inject(this)

        editorViewModel = ViewModelProvider(requireActivity(), editorViewModelFactory)
            .get(EditorViewModel::class.java)

        colorPickerViewModel = ViewModelProvider(requireActivity())
            .get(ColorPickerViewModel::class.java)
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

        colorPickerViewModel.colorLiveData.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                editorViewModel.editor.color = it
            }
        })

        binding.prioritySpinner.onItemSelectedListener = editorViewModel.onPrioritySelectedListener

        binding.colorWidget.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_nav_editor_to_color_picker_dialog)
        }
    }
}
