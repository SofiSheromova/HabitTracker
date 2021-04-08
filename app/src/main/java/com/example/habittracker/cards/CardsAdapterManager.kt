package com.example.habittracker.cards

import com.example.habittracker.model.Card


class CardsAdapterManager(
    private val cards: MutableList<Card>,
    private val onItemClickListener: CardsAdapter.OnItemClickListener,
) {
    private val adapters = mutableListOf<CardsAdapter>()

    fun createAdapter(): CardsAdapter {
        val adapter = CardsAdapter(cards, onItemClickListener)
        adapters.add(adapter)
        return adapter
    }

    fun createFilterAdapter(filter: (Card) -> Boolean): CardsFilterAdapter {
        val adapter = CardsFilterAdapter(cards, onItemClickListener, filter)
        adapters.add(adapter)
        return adapter
    }

    fun notifyItemChanged() {
        for (adapter in adapters) {
            adapter.notifyDataSetChanged()
        }
    }
}
