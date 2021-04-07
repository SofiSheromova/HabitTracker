package com.example.habittracker.cards

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.databinding.FragmentCardsOldBinding

interface HasCardsAdapterManager {
    val adapterManager: CardsAdapterManager
}

internal interface OnFragmentSendDataListener {
    fun onSendCard(selectedItem: Card)
}

open class BaseCardsFragment : Fragment(), CardsAdapter.OnItemClickListener {
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

    override fun onItemClicked(card: Card) {
        fragmentSendDataListener.onSendCard(card)
    }
}