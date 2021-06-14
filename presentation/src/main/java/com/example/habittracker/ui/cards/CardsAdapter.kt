package com.example.habittracker.ui.cards

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
import com.example.domain.model.Priority
import com.example.habittracker.R
import com.example.habittracker.model.PeriodicityFormatter

class CardsAdapter(
    private val cardsLiveData: LiveData<List<Habit>>,
    private val onItemClickListener: OnItemClickListener,
    private val periodicityFormatter: PeriodicityFormatter
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder?>() {
    private val habits: List<Habit>
        get() = cardsLiveData.value ?: listOf()

    interface OnItemClickListener {
        fun onItemClicked(habit: Habit)
        fun onDoneButtonClicked(habit: Habit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)
        return CardViewHolder(view, periodicityFormatter)
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

    class CardViewHolder(
        itemView: View,
        private val periodicityFormatter: PeriodicityFormatter
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
            periodicity.text = periodicityFormatter.periodicityToString(habit.periodicity)

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
    }
}