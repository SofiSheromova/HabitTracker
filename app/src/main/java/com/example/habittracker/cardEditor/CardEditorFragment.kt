package com.example.habittracker.cardEditor

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import cards
import com.example.habittracker.R
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.Periodicity
import com.example.habittracker.cards.Type
import com.example.habittracker.databinding.FragmentCardEditorBinding

class CardEditorFragment : Fragment() {

    companion object {
        const val CARD_ID = "CARD_ID"

        @JvmStatic
        fun newInstance(cardId: String?) =
            CardEditorFragment().apply {
                arguments = Bundle().apply {
                    putString(CARD_ID, cardId)
                }
            }
    }

    private lateinit var binding: FragmentCardEditorBinding

    private lateinit var fragmentSendDataListener: OnFragmentSendDataListener
    private lateinit var state: Card
    private var originalCard: Card? = null

    internal interface OnFragmentSendDataListener {
        fun onFormSubmit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement the interface " +
                        "CardEditorFragment.OnFragmentSendDataListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardId = arguments?.getString(CARD_ID)
        originalCard = cards.find { it.id == cardId }
        state = originalCard?.copy() ?: Card()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardEditorBinding.inflate(inflater, container, false)
        val view = binding.root

        fillFieldsWithValues(state)

        binding.titleEdit.addTextChangedListener(getTitleTextWatcher(state))
        binding.descriptionEdit.addTextChangedListener(getDescriptionTextWatcher(state))
        binding.typeRadioGroup.setOnCheckedChangeListener(getTypeChangeListener(state))
        binding.prioritySeekBar.setOnSeekBarChangeListener(getPriorityChangeListener(state))
        binding.repetitionsNumberEdit.addTextChangedListener(getRepetitionsNumberTextWatcher(state))
        binding.daysNumberEdit.addTextChangedListener(getDaysNumberTextWatcher(state))
        binding.submitButton.setOnClickListener(getSubmitClickListener(state))

        return view
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
            fragmentSendDataListener.onFormSubmit()
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
