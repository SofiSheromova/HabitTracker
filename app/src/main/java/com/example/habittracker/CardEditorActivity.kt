package com.example.habittracker


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.cards.Card
import com.example.habittracker.databinding.ActivityCardEditorBinding
import org.json.JSONObject

class CardEditorActivity : AppCompatActivity(), CardEditorFragment.OnFragmentSendDataListener {
    private lateinit var binding: ActivityCardEditorBinding
    var selectedItem: Card? = null
    var selectedIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val extras = intent.extras
        if (extras != null) {
            val cardJSON = extras.getString(Constants.CARD_JSON)
            selectedIndex = extras.getInt(Constants.CARD_POSITION)
            if (cardJSON != null)
                selectedItem = Card.fromJSON(JSONObject(cardJSON))
        }

        supportFragmentManager.beginTransaction()
            .add(
                R.id.card_editor_fragment,
                CardEditorFragment.newInstance(selectedItem ?: Card(), selectedIndex)
            )
            .commit()
    }

    override fun onResume() {
        super.onResume()
        val fragment = supportFragmentManager
            .findFragmentById(R.id.card_editor_fragment) as CardEditorFragment?
        fragment?.setSelectedCard(selectedItem)
    }

    override fun onSendData(card: Card, cardPosition: Int) {
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