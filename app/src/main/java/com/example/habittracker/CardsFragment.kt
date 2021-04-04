package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.CardsAdapter
import com.example.habittracker.databinding.FragmentCardsBinding
import org.json.JSONObject


class CardsFragment : Fragment() {
    private lateinit var binding: FragmentCardsBinding
    private var fragmentSendDataListener: OnFragmentSendDataListener? = null
    private var cards = mutableListOf<Card>()
    private lateinit var adapter: CardsAdapter

    internal interface OnFragmentSendDataListener {
        fun onSendCard(selectedItem: Card?, selectedIndex: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать интерфейс OnFragmentSendDataListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(
            inflater, container, false
        )
        val view = binding.root
        // val view = inflater.inflate(R.layout.fragment_cards, container, false)

        val cardsRecycler = binding.cardsList
        cardsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter = CardsAdapter(getCardClickListener(cardsRecycler), cards)
        cardsRecycler.adapter = adapter
        return view
    }

    private fun getCardClickListener(cardsRecycler: RecyclerView): View.OnClickListener {
        return View.OnClickListener { view ->
            if (view == null) return@OnClickListener
            val itemPosition: Int = cardsRecycler.getChildLayoutPosition(view)
            // TODO: не трогать cards...
            val item = cards[itemPosition]
//            fragmentSendDataListener?.onSendCard(item, itemPosition)
            val sendIntent = Intent(context, DetailActivity::class.java).apply {
                val bundle = Bundle().apply {
                    putString(Constants.CARD_JSON, item.toJSON().toString())
                    putInt(Constants.CARD_POSITION, itemPosition)
                }
                putExtras(bundle)
            }
            activity?.startActivityForResult(sendIntent, Constants.EDIT_CARD)
        }
    }


    public fun addCard(card: Card) {
        adapter.addCards(card)
        adapter.notifyDataSetChanged()
    }

    public fun editCard(cardPosition: Int, card: Card) {
        adapter.editCard(cardPosition, card)
        adapter.notifyDataSetChanged()
    }
}