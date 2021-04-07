package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.habittracker.cardCollections.CardCollectionsFragment
import com.example.habittracker.cardCollections.OnFragmentSendDataListener
import com.example.habittracker.cardEditor.CardEditorActivity
import com.example.habittracker.cards.Card
import com.example.habittracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
//    CardEditorFragment.OnFragmentSendDataListener,
    OnFragmentSendDataListener {
    companion object {
        private const val CREATE_CARD = 56
        private const val EDIT_CARD = 45
    }

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
        val cardCollectionsFragment = supportFragmentManager
            .findFragmentById(R.id.cards_fragment) as CardCollectionsFragment?

        when (requestCode) {
            CREATE_CARD -> {
//                val card = Card.fromJsonOrNull(data?.getStringExtra(CardEditorActivity.CARD_ID))
//                if (cardsFragment != null && card != null)
//                    cardsFragment.addCard(card)
                cardCollectionsFragment?.adapterManager?.notifyItemChanged()
            }
            EDIT_CARD -> {
//                val card = Card.fromJsonOrNull(data?.getStringExtra(CardEditorActivity.CARD_ID))
//                val position = data?.getIntExtra(CardEditorActivity.CARD_POSITION, -1) ?: -1
//                if (cardsFragment != null && card != null && position != -1) {
//                    cardsFragment.editCard(position, card)
//                }
                cardCollectionsFragment?.adapterManager?.notifyItemChanged()
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
            startActivityForResult(intent, CREATE_CARD)
        }
    }

    override fun onSendCard(selectedItem: Card) {
        val intent = Intent(this, CardEditorActivity::class.java).apply {
            val bundle = Bundle().apply {
                putString(CardEditorActivity.CARD_ID, selectedItem.id)
            }
            putExtras(bundle)
        }
        startActivityForResult(intent, EDIT_CARD)
    }
//
//    override fun onSendData(card: Card, cardPosition: Int) {
//        val cardsFragment = supportFragmentManager
//            .findFragmentById(R.id.cards_fragment) as CardsFragment?
//
//        if (cardPosition >= 0) {
//            cardsFragment?.editCard(cardPosition, card)
//        } else {
//            cardsFragment?.addCard(card)
//        }
//    }
}