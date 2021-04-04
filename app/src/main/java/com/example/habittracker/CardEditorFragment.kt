package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.Periodicity
import com.example.habittracker.cards.Type
import com.example.habittracker.databinding.FragmentCardEditorBinding
import org.json.JSONObject

class CardEditorFragment : Fragment() {
    private lateinit var binding: FragmentCardEditorBinding
    private var fragmentSendDataListener: OnFragmentSendDataListener? = null
    private lateinit var card: Card
    private var cardPosition: Int = -1

    internal interface OnFragmentSendDataListener {
        fun onSendData(card: Card, cardPosition: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать интерфейс CardEditorFragment.OnFragmentSendDataListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = extractCardFrom(arguments)
        card = data.value
        cardPosition = data.position
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardEditorBinding.inflate(inflater, container, false)
        val view = binding.root
//        inflater.inflate(R.layout.fragment_card_editor, container, false)

        fillFieldsWithValues(card)

        binding.titleEdit.addTextChangedListener(getTitleTextWatcher(card))
        binding.descriptionEdit.addTextChangedListener(getDescriptionTextWatcher(card))
        binding.typeRadioGroup.setOnCheckedChangeListener(getTypeChangeListener(card))
        binding.prioritySeekBar.setOnSeekBarChangeListener(getPriorityChangeListener(card))
        binding.repetitionsNumberEdit.addTextChangedListener(getRepetitionsNumberTextWatcher(card))
        binding.daysNumberEdit.addTextChangedListener(getDaysNumberTextWatcher(card))
        binding.submitButton.setOnClickListener(getSubmitClickListener(card, cardPosition))

        return view
    }

    // обновление текстового поля
    fun setSelectedItem(selectedItem: Card?) {
        if (selectedItem != null)
            fillFieldsWithValues(selectedItem)
        else
            fillFieldsWithValues(Card())
//        val view = view?.findViewById<View>(R.id.detailsText) as TextView
//        view.text = selectedItem
    }

    private fun extractCardFrom(bundle: Bundle?): Item2<Card> {
        var myCard: Card? = null
        var cardIndex: Int = -1
        bundle?.let {
            val cardJSON = it.getString(Constants.CARD_JSON)
            cardIndex = it.getInt(Constants.CARD_POSITION, -1)
            if (cardJSON != null)
                myCard = Card.fromJSON(JSONObject(cardJSON))
        }
        return Item2(cardIndex, myCard ?: Card())
    }

    companion object {
        @JvmStatic
        fun newInstance(card: Card, cardPosition: Int) =
            CardEditorFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.CARD_JSON, card.toJSON().toString())
                    putInt(Constants.CARD_POSITION, cardPosition)
                }
            }
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
            Log.d("TAG2", card.toJSON().toString())
            fragmentSendDataListener?.onSendData(card, cardPosition)
//            val intent = Intent().apply {
//                val bundle = Bundle().apply {
//                    putString(Constants.CARD_JSON, card.toJSON().toString())
//                    putInt(Constants.CARD_POSITION, cardPosition)
//                }
//                putExtras(bundle)
//            }
//            setResult(AppCompatActivity.RESULT_OK, intent)
//            finish()
        }
    }

    private val getRepetitionsNumberTextWatcher = { card: Card ->
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("TAG2", card.toJSON().toString())
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

data class Item2<T>(val position: Int, val value: T)