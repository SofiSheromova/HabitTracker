package com.example.habittracker.cardCollections

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.cards.*
import com.example.habittracker.databinding.FragmentCardsOldBinding

// Instances of this class are fragments representing a single
// object in our collection.
class NewCardsFragment : Fragment() {

    companion object {
        const val ARG_OBJECT = "object"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cards_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(R.id.text1)
            textView.text = getInt(ARG_OBJECT).toString()
        }
    }
}

interface HasCardsAdapterManager {
    val adapterManager: CardsAdapterManager
}

class GoodCardsFragment : BaseCardsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = adapterManager.createFilterAdapter { it.type == Type.GOOD }
    }
}

class BadCardsFragment : BaseCardsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = adapterManager.createFilterAdapter { it.type == Type.BAD }
    }
}

internal interface OnFragmentSendDataListener {
    fun onSendCard(selectedItem: Card)
}

open class BaseCardsFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentCardsOldBinding
    private lateinit var fragmentSendDataListener: OnFragmentSendDataListener
    protected lateinit var adapterManager: CardsAdapterManager
    protected lateinit var adapter: CardsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendDataListener = parentFragment as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$parentFragment must implement the interface " +
                        "CardsFragment.OnFragmentSendDataListener"
            )
        }

        try {
            adapterManager = (parentFragment as HasCardsAdapterManager).adapterManager
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$parentFragment must implement the interface HasCardsAdapterManager"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = adapterManager.createAdapter()
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

//    fun addCard(card: Card) {
//        fragmentSendDataListener?.addCard(card)
//    }

//    fun editCard(cardPosition: Int, card: Card) {
//        fragmentSendDataListener?.editCard(cardPosition, card)
//    }

    override fun onItemClicked(card: Card) {
//        val cardIndex = adapter.indexOf(card)
        fragmentSendDataListener.onSendCard(card)
    }
}