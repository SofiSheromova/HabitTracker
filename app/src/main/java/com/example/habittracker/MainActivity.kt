package com.example.habittracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.cards.Card
import com.example.habittracker.cards.CardsAdapter
import com.example.habittracker.cards.Periodicity
import com.example.habittracker.cards.Type
import com.example.habittracker.databinding.ActivityMainBinding

val cards = mutableListOf(
    Card(
        "title1",
        "description1",
        Periodicity(1, 1),
        Type.GOOD,
        0,
        "#ebffe3"
    ),
    Card(
        "title2",
        "description2",
        Periodicity(1, 2),
        Type.BAD,
        1,
        "#e3feff"
    ),
    Card(
        "title3",
        "description3",
        Periodicity(1, 3),
        Type.GOOD,
        3,
        "#ffe3f2"
    ),
)
//val cards = mutableListOf<Card>()

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val cardsRecycler: RecyclerView = binding.cardsView
        cardsRecycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        adapter = CardsAdapter(getCardClickListener(cardsRecycler), cards)
        cardsRecycler.adapter = adapter
    }

    private fun getCardClickListener(cardsRecycler: RecyclerView): View.OnClickListener {
        return View.OnClickListener { view ->
            if (view == null) return@OnClickListener
        }
    }
}