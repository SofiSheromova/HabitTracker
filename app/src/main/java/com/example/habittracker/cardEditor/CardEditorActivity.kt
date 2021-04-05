package com.example.habittracker.cardEditor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.R
import com.example.habittracker.cards.Card
import com.example.habittracker.databinding.ActivityCardEditorBinding


class CardEditorActivity : AppCompatActivity(), CardEditorFragment.OnFragmentSendDataListener {
    companion object {
        const val CARD_JSON = "CARD_JSON"
        const val CARD_POSITION = "POSITION"
    }

    private lateinit var binding: ActivityCardEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null)
            addCardEditorFragment()
    }

    override fun onSendData(card: Card, cardPosition: Int) {
        val intent = Intent().apply {
            val bundle = Bundle().apply {
                putString(CardEditorFragment.CARD_JSON, card.toJSON().toString())
                putInt(CardEditorFragment.CARD_POSITION, cardPosition)
            }
            putExtras(bundle)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun addCardEditorFragment() {
        val extras = intent.extras
        val selectedIndex = extras?.getInt(CARD_POSITION) ?: -1
        val selectedItem: Card? = if (extras != null) {
            Card.fromJsonOrNull(extras.getString(CARD_JSON))
        } else {
            null
        }

        supportFragmentManager.beginTransaction()
            .add(
                R.id.card_editor_fragment,
                CardEditorFragment.newInstance(selectedItem ?: Card(), selectedIndex)
            )
            .commit()
    }
}