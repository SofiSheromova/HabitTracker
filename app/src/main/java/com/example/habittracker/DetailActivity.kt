package com.example.habittracker


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.cards.Card
import org.json.JSONObject

class DetailActivity : AppCompatActivity(), CardEditorFragment.OnFragmentSendDataListener {
    var selectedItem: Card? = null
    var selectedIndex: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            finish()
//            return
//        }
        setContentView(R.layout.activity_detail)

        val extras = intent.extras
        if (extras != null) {
//            selectedItem = extras.getString(SELECTED_ITEM)
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
        Log.d("HELP", fragment.toString())
        fragment?.setSelectedItem(selectedItem)
    }

    override fun onSendData(card: Card, cardPosition: Int) {
        Log.d("HELP", card.toJSON().toString())
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