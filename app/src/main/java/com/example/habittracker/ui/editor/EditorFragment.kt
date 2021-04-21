package com.example.habittracker.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentEditorBinding
import com.example.habittracker.model.Card
import com.example.habittracker.model.Type
import com.example.habittracker.ui.cards.CardsViewModel

class EditorFragment : Fragment() {
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var editorViewModel: EditorViewModel
    private lateinit var binding: FragmentEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardsViewModel = ViewModelProvider(requireActivity())
            .get(CardsViewModel::class.java)
        editorViewModel = ViewModelProvider(requireActivity())
            .get(EditorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editorViewModel.cardLiveData.observe(viewLifecycleOwner, {
            fillFieldsWithValues(it)
        })

        binding.titleEdit.addTextChangedListener(getTitleTextWatcher())
        binding.descriptionEdit.addTextChangedListener(getDescriptionTextWatcher())
        binding.typeRadioGroup.setOnCheckedChangeListener(getTypeChangeListener())
        binding.prioritySeekBar.setOnSeekBarChangeListener(getPriorityChangeListener())
        binding.repetitionsNumberEdit.addTextChangedListener(getRepetitionsNumberTextWatcher())
        binding.daysNumberEdit.addTextChangedListener(getDaysNumberTextWatcher())
        binding.submitButton.setOnClickListener(getSubmitClickListener())
    }

    private fun fillFieldsWithValues(card: Card) {
        binding.titleEdit.setText(card.title)

        binding.descriptionEdit.setText(card.description)

        if (card.type == Type.GOOD) {
            binding.radioGood.isChecked = true
        } else {
            binding.radioBad.isChecked = true
        }

        binding.prioritySeekBar.progress = card.priority

        binding.repetitionsNumberEdit.setText(card.periodicity.repetitionsNumber.toString())
        binding.daysNumberEdit.setText(card.periodicity.daysNumber.toString())
    }

    private val getTitleTextWatcher = {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                when {
                    content.isEmpty() -> {
                        binding.titleEdit.error = resources.getString(R.string.empty_field)
                    }
                    content.length > 10 -> {
                        binding.titleEdit.error = resources.getString(R.string.maximum_length)
                    }
                    else -> {
                        editorViewModel.setTitle(binding.titleEdit.text.toString())
                    }
                }
            }
        }
    }

    private val getDescriptionTextWatcher = {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                when {
                    content.length > 100 -> {
                        binding.descriptionEdit.error = resources.getString(R.string.maximum_length)
                    }
                    else -> {
                        editorViewModel.setDescription(binding.descriptionEdit.text.toString())
                    }
                }
            }
        }
    }

    private val getTypeChangeListener = {
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group!!.findViewById(checkedId) as RadioButton
            val index = group.indexOfChild(checkedRadioButton)
            editorViewModel.setType(index)
        }
    }

    private val getPriorityChangeListener = {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                editorViewModel.setPriority(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
    }

    private val getRepetitionsNumberTextWatcher = {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                if (numberFieldValidator(binding.repetitionsNumberEdit)) {
                    editorViewModel.setRepetitionsNumber(content.toInt())
                }
            }
        }
    }

    private val getDaysNumberTextWatcher = {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                if (numberFieldValidator(binding.daysNumberEdit)) {
                    editorViewModel.setDaysNumber(content.toInt())
                }
            }
        }
    }

    private fun numberFieldValidator(field: EditText): Boolean {
        val content = field.text.toString()
        var isValid = true
        when {
            content.isEmpty() -> {
                field.error = resources.getString(R.string.empty_field)
                isValid = false
            }
            content == "0" -> {
                field.error = resources.getString(R.string.invalid_value)
                isValid = false
            }
        }
        return isValid
    }

    private val getSubmitClickListener = {
        View.OnClickListener {
            editorViewModel.updateCard()
            cardsViewModel.refreshValue()

            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}