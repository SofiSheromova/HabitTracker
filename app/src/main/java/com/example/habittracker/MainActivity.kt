package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.Periodicity
import com.example.habittracker.cards.Type
import com.example.habittracker.databinding.ActivityMainBinding
import org.json.JSONObject

val cards = mutableListOf(
    Card(
        "title1",
        "description1",
        Periodicity(1, 1),
        Type.GOOD,
        0,
        "#ebffe3"
    ),
    Card(
        "title2",
        "description2",
        Periodicity(1, 2),
        Type.BAD,
        1,
        "#e3feff"
    ),
    Card(
        "title3",
        "description3",
        Periodicity(1, 3),
        Type.GOOD,
        3,
        "#ffe3f2"
    ),
)
//val cards = mutableListOf<Card>()

class MainActivity : AppCompatActivity(),
    CardsFragment.OnFragmentSendDataListener,
    CardEditorFragment.OnFragmentSendDataListener {
    private lateinit var binding: ActivityMainBinding
    private var cardEditTransaction: FragmentTransaction? = null
//    private lateinit var adapter: CardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        val cardsRecycler: RecyclerView = binding.cardsView
//        cardsRecycler.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//
//        adapter = CardsAdapter(getCardClickListener(cardsRecycler), cards)
//        cardsRecycler.adapter = adapter

        setupCardCreateButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        // TODO может куда-то перенести? Если ли смысл?
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

//    private fun getCardClickListener(cardsRecycler: RecyclerView): View.OnClickListener {
//        return View.OnClickListener { view ->
//            if (view == null) return@OnClickListener
//            val itemPosition: Int = cardsRecycler.getChildLayoutPosition(view)
//            // TODO: не трогать cards...
//            val item: Card = cards[itemPosition]
//            val sendIntent = Intent(this, CardEditorActivity::class.java).apply {
//                val bundle = Bundle().apply {
//                    putString(Constants.CARD_JSON, item.toJSON().toString())
//                    putInt(Constants.CARD_POSITION, itemPosition)
//                }
//                putExtras(bundle)
//            }
//            startActivityForResult(sendIntent, Constants.EDIT_CARD)
//        }
//    }

    private fun setupCardCreateButton() {
        val icon = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_add_24,
            null
        )
        binding.cardCreateButton.setImageDrawable(icon)
//        binding.cardCreateButton.setOnClickListener {
//            if (it == null)
//                return@setOnClickListener
//            val sendIntent = Intent(this, CardEditorActivity::class.java)
//            startActivityForResult(sendIntent, Constants.CREATE_CARD)
//        }

        binding.cardCreateButton.setOnClickListener {
            if (it == null)
                return@setOnClickListener
//            cardEditTransaction = supportFragmentManager.beginTransaction()
//            cardEditTransaction!!
//                .add(
//                    R.id.card_editor_fragment,
//                    CardEditorFragment.newInstance(Card(), -1)
//                )
//                .commit()
            val intent = Intent(
                applicationContext,
                DetailActivity::class.java
            )
//            intent.putExtra(DetailActivity.SELECTED_ITEM, selectedItem?.toJSON().toString())
            startActivityForResult(intent, Constants.CREATE_CARD)
        }
    }

    override fun onSendCard(selectedItem: Card?, selectedIndex: Int) {
        val fragment = supportFragmentManager
            .findFragmentById(R.id.cards_fragment) as CardEditorFragment?
        if (fragment != null && fragment.isVisible) {
            fragment.setSelectedItem(selectedItem)
        } else {
//            supportFragmentManager.beginTransaction()
//                .add(
//                    R.id.card_editor_fragment,
//                    CardEditorFragment.newInstance(selectedItem ?: Card(), selectedIndex)
//                )
//                .commit()
            val intent = Intent(
                applicationContext,
                DetailActivity::class.java
            ).apply {
                val bundle = Bundle().apply {
                    putString(Constants.CARD_JSON, selectedItem?.toJSON().toString())
                    putInt(Constants.CARD_POSITION, selectedIndex)
                }
                putExtras(bundle)
            }
            startActivityForResult(intent, Constants.EDIT_CARD)
        }
    }

    override fun onSendData(card: Card, cardPosition: Int) {
//        val intent = Intent().apply {
//            val bundle = Bundle().apply {
//                putString(Constants.CARD_JSON, card.toJSON().toString())
//                putInt(Constants.CARD_POSITION, cardPosition)
//            }
//            putExtras(bundle)
//        }
//        setResult(RESULT_OK, intent)
//        finish()

//        val cardEditorFragment = supportFragmentManager
//            .findFragmentById(R.id.card_editor_fragment) as CardEditorFragment
//        cardEditTransaction
//            ?.remove(cardEditorFragment)
//            ?.addToBackStack(null)
//            ?.commit()


        // TODO может куда-то перенести? Если ли смысл?
        val cardsFragment = supportFragmentManager
            .findFragmentById(R.id.cards_fragment) as CardsFragment?

        if (cardPosition >= 0) {
            cardsFragment?.editCard(cardPosition, card)
        } else {
            cardsFragment?.addCard(card)
        }
    }
}