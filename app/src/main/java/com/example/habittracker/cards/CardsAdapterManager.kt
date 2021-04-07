package com.example.habittracker.cards


class CardsAdapterManager(
    private val cards: MutableList<Card>,
    private val onItemClickListener: OnItemClickListener,
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

    fun addCard(card: Card) {
        cards.add(card)
        for (adapter in adapters) {
            adapter.notifyDataSetChanged()
        }
    }

    fun editCard(position: Int, card: Card) {
        cards[position] = card
        for (adapter in adapters) {
            adapter.notifyDataSetChanged()
        }
    }

    fun notifyItemChanged() {
        for (adapter in adapters) {
            adapter.notifyDataSetChanged()
        }
    }
}

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
