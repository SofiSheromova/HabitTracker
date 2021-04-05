package com.example.habittracker.cards

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.CardsAdapter
import com.example.habittracker.cards.OnItemClickListener
import com.example.habittracker.databinding.FragmentCardsOldBinding

val cards = mutableListOf<Card>()

class CardsFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentCardsOldBinding
    private var fragmentSendDataListener: OnFragmentSendDataListener? = null
    private lateinit var adapter: CardsAdapter

    internal interface OnFragmentSendDataListener {
        fun onSendCard(selectedItem: Card, selectedIndex: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement the interface " +
                        "CardsFragment.OnFragmentSendDataListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CardsAdapter(cards, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsOldBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cardsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.cardsRecycler.adapter = adapter
        return view
    }

    fun addCard(card: Card) {
        adapter.addCards(card)
        adapter.notifyItemInserted(adapter.itemCount - 1)
//        adapter.notifyDataSetChanged()
    }

    fun editCard(cardPosition: Int, card: Card) {
        adapter.editCard(cardPosition, card)
        adapter.notifyItemChanged(cardPosition)
//        adapter.notifyDataSetChanged()
    }

    override fun onItemClicked(card: Card, position: Int) {
        fragmentSendDataListener?.onSendCard(card, position)
    }
}