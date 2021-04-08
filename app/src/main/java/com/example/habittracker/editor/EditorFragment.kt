package com.example.habittracker.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import cards
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentCardEditorBinding
import com.example.habittracker.model.Card
import com.example.habittracker.model.Periodicity
import com.example.habittracker.model.Type

class EditorFragment : Fragment() {

    private val args by navArgs<EditorFragmentArgs>()

    private lateinit var binding: FragmentCardEditorBinding

    private lateinit var state: Card
    private var originalCard: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalCard = args.card
        state = originalCard?.copy() ?: Card()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardEditorBinding.inflate(inflater, container, false)

        fillFieldsWithValues(state)

        binding.titleEdit.addTextChangedListener(getTitleTextWatcher(state))
        binding.descriptionEdit.addTextChangedListener(getDescriptionTextWatcher(state))
        binding.typeRadioGroup.setOnCheckedChangeListener(getTypeChangeListener(state))
        binding.prioritySeekBar.setOnSeekBarChangeListener(getPriorityChangeListener(state))
        binding.repetitionsNumberEdit.addTextChangedListener(getRepetitionsNumberTextWatcher(state))
        binding.daysNumberEdit.addTextChangedListener(getDaysNumberTextWatcher(state))
        binding.submitButton.setOnClickListener(getSubmitClickListener(state))

        return binding.root
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

    private val getTitleTextWatcher = { card: Card ->
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
                        card.title = binding.titleEdit.text.toString()
                    }
                }
            }
        }
    }

    private val getDescriptionTextWatcher = { card: Card ->
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
                        card.description = binding.descriptionEdit.text.toString()
                    }
                }
            }
        }
    }

    private val getTypeChangeListener = { card: Card ->
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group!!.findViewById(checkedId) as RadioButton
            card.type = Type.valueOf(group.indexOfChild(checkedRadioButton))
        }
    }

    private val getPriorityChangeListener = { card: Card ->
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                card.priority = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
    }

    private val getSubmitClickListener = { card: Card ->
        View.OnClickListener {
            val index = if (originalCard != null) {
                cards.indexOf(originalCard)
            } else {
                -1
            }
            if (index != -1) {
                cards[index] = card
            } else {
                cards.add(card)
            }
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    private val getRepetitionsNumberTextWatcher = { card: Card ->
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                if (numberFieldValidator(binding.repetitionsNumberEdit)) {
                    card.periodicity = Periodicity(content.toInt(), card.periodicity.daysNumber)
                }
            }
        }
    }

    private val getDaysNumberTextWatcher = { card: Card ->
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()
                if (numberFieldValidator(binding.daysNumberEdit)) {
                    card.periodicity =
                        Periodicity(card.periodicity.repetitionsNumber, content.toInt())
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
}