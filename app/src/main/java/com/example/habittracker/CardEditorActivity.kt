package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.Periodicity
import com.example.habittracker.cards.Type
import com.example.habittracker.databinding.ActivityCardEditorBinding
import org.json.JSONObject

class CardEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val (cardPosition, card) = extractCardFromBundle()

        val typesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        )
        fillFieldsWithValues(card, typesAdapter)

        binding.titleEdit.addTextChangedListener(getTitleTextWatcher(card))
        binding.descriptionEdit.addTextChangedListener(getDescriptionTextWatcher(card))
        binding.typeRadioGroup.setOnCheckedChangeListener(getTypeChangeListener(card))
        binding.prioritySeekBar.setOnSeekBarChangeListener(getPriorityChangeListener(card))
        binding.submitButton.setOnClickListener(getSubmitClickListener(card, cardPosition))
        binding.repetitionsNumberEdit.addTextChangedListener(getRepetitionsNumberTextWatcher(card))
        binding.daysNumberEdit.addTextChangedListener(getDaysNumberTextWatcher(card))
    }

    private fun extractCardFromBundle(): Item<Card> {
        val cardJSON = intent.getStringExtra(Constants.CARD_JSON)
        val cardPosition = intent.getIntExtra(Constants.CARD_POSITION, -1)
        val card: Card = if (cardJSON != null) Card.fromJSON(JSONObject(cardJSON)) else Card()
        return Item(cardPosition, card)
    }

    private fun fillFieldsWithValues(card: Card, typesAdapter: ArrayAdapter<String>) {
        binding.titleEdit.setText(card.title)

        binding.descriptionEdit.setText(card.description)

        if (card.type == Type.GOOD) {
            binding.radioGood.isChecked = true
        } else {
            binding.radioBad.isChecked = true
        }

        binding.prioritySeekBar.progress = card.priority
        Log.d(
            "TAGGG",
            "${card.priority}, ${binding.prioritySeekBar.progress}, ${binding.prioritySeekBar.max}"
        )

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
            Log.d("TAG", group.indexOfChild(checkedRadioButton).toString())
            card.type = Type.valueOf(group.indexOfChild(checkedRadioButton))
            Log.d("TAG", card.type.toString())
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

    private val getSubmitClickListener = { card: Card, cardPosition: Int ->
        View.OnClickListener {
            Log.d("TAG", card.toJSON().toString())
            val intent = Intent().apply {
                val bundle = Bundle().apply {
                    putString(Constants.CARD_JSON, card.toJSON().toString())
                    putInt(Constants.CARD_POSITION, cardPosition)
                }
                putExtras(bundle)
            }
            setResult(RESULT_OK, intent)
            finish()
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
                when {
                    content.isEmpty() -> {
                        binding.repetitionsNumberEdit.error =
                            resources.getString(R.string.empty_field)
                    }
                    content == "0" -> {
                        binding.repetitionsNumberEdit.error =
                            resources.getString(R.string.invalid_value)
                    }
                    else -> {
                        card.periodicity = Periodicity(content.toInt(), card.periodicity.daysNumber)
                    }
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
                when {
                    content.isEmpty() -> {
                        binding.daysNumberEdit.error = resources.getString(R.string.empty_field)
                    }
                    content == "0" -> {
                        binding.daysNumberEdit.error = resources.getString(R.string.invalid_value)
                    }
                    else -> {
                        card.periodicity =
                            Periodicity(card.periodicity.repetitionsNumber, content.toInt())
                    }
                }
            }
        }
    }
}

data class Item<T>(val position: Int, val value: T)