package com.example.habittracker.model

import androidx.lifecycle.LiveData

class CardRepository(private val cardDao: CardDao) {
    val allCards: LiveData<List<Card>> = cardDao.getAll()

    fun insertAll(vararg cards: Card) {
        for (card in cards)
        cardDao.insertAll(*cards)
    }

    fun update(id: Long, card: Card) {
        card.id = id
        cardDao.updateAll(card)
    }
}