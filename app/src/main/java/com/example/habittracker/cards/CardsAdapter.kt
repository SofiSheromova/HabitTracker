package com.example.habittracker.cards

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R


class CardsAdapter(
    private val cards: MutableList<Card>,
    private val onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<CardsAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: Card = cards[position]
        holder.bind(card, position, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun addCards(vararg items: Card) {
        cards.addAll(items)
    }

    fun editCard(position: Int, card: Card) {
        cards[position] = card
    }

    operator fun get(itemPosition: Int): Card? {
        return cards.getOrNull(itemPosition)
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        var description: TextView = itemView.findViewById<View>(R.id.card_description) as TextView
        var priority: TextView = itemView.findViewById<View>(R.id.card_priority) as TextView
        var periodicity: TextView = itemView.findViewById<View>(R.id.card_periodicity) as TextView
        var like: ImageView = itemView.findViewById<View>(R.id.like_icon) as ImageView
        var dislike: ImageView = itemView.findViewById<View>(R.id.dislike_icon) as ImageView

        var card: CardView = itemView.findViewById<View>(R.id.card) as CardView
        val setColor = { color: String? ->
            try {
                card.setCardBackgroundColor(Color.parseColor(color))
            } catch (e: IllegalArgumentException) {
            }
        }

        fun bind(card: Card, position: Int, clickListener: OnItemClickListener) {
            title.text = card.title
            description.text = card.description
            priority.text = card.priority.toString()
            periodicity.text = card.periodicity.toString()

            if (card.type == Type.GOOD) {
                like.visibility = View.VISIBLE
                dislike.visibility = View.INVISIBLE
            } else {
                like.visibility = View.INVISIBLE
                dislike.visibility = View.VISIBLE
            }

            setColor(card.color)

            itemView.setOnClickListener {
                clickListener.onItemClicked(card, position)
            }
        }
    }
}


interface OnItemClickListener {
    fun onItemClicked(card: Card, position: Int)
}