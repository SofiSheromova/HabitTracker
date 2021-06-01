package com.example.habittracker.ui.cards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Type
import com.example.habittracker.R

class CardsAdapter(
    private val cardsLiveData: LiveData<List<Habit>>,
    private val onItemClickListener: OnItemClickListener,
    private val context: Context,
    private val filter: ((Habit) -> Boolean)? = null
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder?>() {
    private val habits: List<Habit>
        get() {
            val value = cardsLiveData.value ?: listOf()
            filter?.let { return value.filter(it) }
            return value
        }

    interface OnItemClickListener {
        fun onItemClicked(habit: Habit)
        fun onDoneButtonClicked(habit: Habit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)
        return CardViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val habit: Habit = habits[position]
        holder.bind(habit, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    operator fun get(itemPosition: Int): Habit? {
        return habits.getOrNull(itemPosition)
    }

    fun indexOf(habit: Habit): Int {
        return habits.indexOf(habit)
    }

    class CardViewHolder(
        itemView: View,
        private val context: Context
    ) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        var description: TextView = itemView.findViewById<View>(R.id.card_description) as TextView
        var priority: TextView = itemView.findViewById<View>(R.id.card_priority) as TextView
        var periodicity: TextView = itemView.findViewById<View>(R.id.card_periodicity) as TextView
        var like: ImageView = itemView.findViewById<View>(R.id.like_icon) as ImageView
        var dislike: ImageView = itemView.findViewById<View>(R.id.dislike_icon) as ImageView
        var doneButton: Button = itemView.findViewById<View>(R.id.done_button) as Button

        var card: CardView = itemView.findViewById<View>(R.id.card) as CardView
        val setColor = { color: Int ->
            card.setCardBackgroundColor(color)
        }

        fun bind(habit: Habit, clickListener: OnItemClickListener) {
            title.text = habit.title
            description.text = habit.description
            priority.text = habit.priority.value.toString()
            periodicity.text = formatPeriodicity(habit.periodicity)

            if (habit.type == Type.GOOD) {
                like.visibility = View.VISIBLE
                dislike.visibility = View.INVISIBLE
            } else {
                like.visibility = View.INVISIBLE
                dislike.visibility = View.VISIBLE
            }

            setColor(habit.color)

            itemView.setOnClickListener {
                clickListener.onItemClicked(habit)
            }
            doneButton.setOnClickListener {
                clickListener.onDoneButtonClicked(habit)
            }
        }

        private fun formatPeriodicity(periodicity: Periodicity): String {
            val repetitionsNumber = periodicity.repetitionsNumber
            val daysNumber = periodicity.daysNumber
            return if (repetitionsNumber == 1 && daysNumber == 1) {
                context.resources.getString(R.string.everyday)
            } else {
                "$repetitionsNumber " +
                        context.resources.getString(R.string.times_in) + " " +
                        "$daysNumber " +
                        context.resources.getString(R.string.days)
            }
        }
    }
}