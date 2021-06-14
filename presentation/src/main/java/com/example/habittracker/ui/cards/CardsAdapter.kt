package com.example.habittracker.ui.cards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
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
        val priority: ImageView = itemView.findViewById(R.id.priority_icon)
        var periodicity: TextView = itemView.findViewById<View>(R.id.card_periodicity) as TextView

        var doneButton: Button = itemView.findViewById<View>(R.id.done_button) as Button

        var card: CardView = itemView.findViewById<View>(R.id.card) as CardView
        val setColor = { color: Int ->
            card.setCardBackgroundColor(color)
        }

        fun bind(habit: Habit, clickListener: OnItemClickListener) {
            title.text = habit.title
            description.text = habit.description
            periodicity.text = formatPeriodicity(habit.periodicity)

            when (habit.priority) {
                Priority.LOW -> priority.setImageResource(R.drawable.ic_digit_three_24)
                Priority.MIDDLE -> priority.setImageResource(R.drawable.ic_digit_two_24)
                Priority.HIGH -> priority.setImageResource(R.drawable.ic_digit_one_24)
            }

            setColor(habit.color)

            itemView.setOnClickListener {
                clickListener.onItemClicked(habit)
            }
            doneButton.setOnClickListener { view ->
                if (view != null) {
                    ViewCompat.postOnAnimationDelayed(
                        view,
                        { clickListener.onDoneButtonClicked(habit) },
                        400
                    )
                }
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