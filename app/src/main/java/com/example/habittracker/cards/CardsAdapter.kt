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
    private val onItemClickListener: View.OnClickListener,
    private val cards: MutableList<Card>
) :
    RecyclerView.Adapter<CardsAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)
        view.setOnClickListener(onItemClickListener)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: Card = cards[position]
        holder.title.text = card.title
        holder.description.text = card.description
        holder.priority.text = card.priority.toString()
        holder.periodicity.text = card.periodicity.toString()

        if (card.type == Type.GOOD) {
            holder.like.visibility = View.VISIBLE
            holder.dislike.visibility = View.INVISIBLE
        } else {
            holder.like.visibility = View.INVISIBLE
            holder.dislike.visibility = View.VISIBLE
        }

        holder.setColor(card.color)
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
    }
}
