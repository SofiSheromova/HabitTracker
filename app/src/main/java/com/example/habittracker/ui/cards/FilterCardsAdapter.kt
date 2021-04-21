package com.example.habittracker.ui.cards

import androidx.lifecycle.LiveData
import com.example.habittracker.model.Card

class FilterCardsAdapter(
    cardsLiveData: LiveData<List<Card>>,
    onItemClickListener: OnItemClickListener,
    private val _filter: (Card) -> Boolean
) : CardsAdapter(cardsLiveData, onItemClickListener) {
    override fun getItemCount(): Int {
        return cards.filter(_filter).size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: Card = cards.filter(_filter)[position]
        holder.bind(card, onItemClickListener)
    }

    override operator fun get(itemPosition: Int): Card? {
        return cards.filter(_filter).getOrNull(itemPosition)
    }

    override fun indexOf(card: Card): Int {
        return cards.filter(_filter).indexOf(card)
    }
}