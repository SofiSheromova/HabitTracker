package com.example.habittracker.cards

class CardsFilterAdapter(
    private val cards: MutableList<Card>,
    private val onItemClickListener: OnItemClickListener,
    private val filter: (Card) -> Boolean,
) : CardsAdapter(cards, onItemClickListener) {

    override fun getItemCount(): Int {
        return cards.filter(filter).size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: Card = cards.filter(filter)[position]
        holder.bind(card, onItemClickListener)
    }

    override operator fun get(itemPosition: Int): Card? {
        return cards.filter(filter).getOrNull(itemPosition)
    }

    override fun indexOf(card: Card): Int {
        return cards.filter(filter).indexOf(card)
    }
}