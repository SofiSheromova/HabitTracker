package com.example.habittracker.ui.cards

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.model.Card
import com.example.habittracker.model.Type


class CardsAdapter(
    private val cardsLiveData: LiveData<List<Card>>,
    private val onItemClickListener: OnItemClickListener,
    private val filter: ((Card) -> Boolean)? = null
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder?>() {
    private val cards: List<Card>
        get() {
            val value = cardsLiveData.value ?: listOf()
            filter?.let { return value.filter(it) }
            return value
        }

    interface OnItemClickListener {
        fun onItemClicked(card: Card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: Card = cards[position]
        holder.bind(card, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    operator fun get(itemPosition: Int): Card? {
        return cards.getOrNull(itemPosition)
    }

    fun indexOf(card: Card): Int {
        return cards.indexOf(card)
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
            if (color != null)
                try {
                    card.setCardBackgroundColor(Color.parseColor(color))
                } catch (e: IllegalArgumentException) {
                }
        }

        fun bind(card: Card, clickListener: OnItemClickListener) {
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
                clickListener.onItemClicked(card)
            }
        }
    }
}