package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.habittracker.cards.Card
import com.example.habittracker.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity(),
    CardsFragment.OnFragmentSendDataListener,
    CardEditorFragment.OnFragmentSendDataListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupCardCreateButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        // TODO: дорогая ли это операция? Данный фрагмент добавлен из xml,
        //  есть ли смысл вынести это в поле класса и не искать?
        val cardsFragment = supportFragmentManager
            .findFragmentById(R.id.cards_fragment) as CardsFragment?

        when (requestCode) {
            Constants.CREATE_CARD -> {
                val card = JSONObject(data?.getStringExtra(Constants.CARD_JSON) ?: "{}")
                cardsFragment?.addCard(Card.fromJSON(card))
            }
            Constants.EDIT_CARD -> {
                val card = JSONObject(data?.getStringExtra(Constants.CARD_JSON) ?: "{}")
                val position = data?.getIntExtra(Constants.CARD_POSITION, -1) ?: -1
                if (position != -1) {
                    cardsFragment?.editCard(position, Card.fromJSON(card))
                }
            }
        }
    }

    private fun setupCardCreateButton() {
        val icon = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_add_24,
            null
        )
        binding.cardCreateButton.setImageDrawable(icon)

        binding.cardCreateButton.setOnClickListener {
            val intent = Intent(this, CardEditorActivity::class.java)
            startActivityForResult(intent, Constants.CREATE_CARD)
        }
    }

    override fun onSendCard(selectedItem: Card, selectedIndex: Int) {
        val intent = Intent(this, CardEditorActivity::class.java).apply {
            val bundle = Bundle().apply {
                putString(Constants.CARD_JSON, selectedItem.toJSON().toString())
                putInt(Constants.CARD_POSITION, selectedIndex)
            }
            putExtras(bundle)
        }
        startActivityForResult(intent, Constants.EDIT_CARD)
    }

    override fun onSendData(card: Card, cardPosition: Int) {
        val cardsFragment = supportFragmentManager
            .findFragmentById(R.id.cards_fragment) as CardsFragment?

        if (cardPosition >= 0) {
            cardsFragment?.editCard(cardPosition, card)
        } else {
            cardsFragment?.addCard(card)
        }
    }
}