package com.example.habittracker.cards

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.databinding.FragmentCardsBinding
import com.example.habittracker.model.Card

interface HasCardsAdapterManager {
    val adapterManager: CardsAdapterManager
}

open class BaseCardsFragment : Fragment() {
    private lateinit var binding: FragmentCardsBinding

    protected lateinit var adapterManager: CardsAdapterManager
    protected lateinit var adapter: CardsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cardsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.cardsRecycler.adapter = adapter
        return view
    }
}