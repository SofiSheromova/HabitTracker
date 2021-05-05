package com.example.habittracker.model

import androidx.lifecycle.LiveData

class CardRepository(private val cardDao: CardDao) {
    val allCards: LiveData<List<Card>> = cardDao.getAll()

    suspend fun insertAll(vararg cards: Card) {
        cardDao.insertAll(*cards)
    }

    suspend fun update(original: Card, newState: Card) {
        original.update(newState)
        cardDao.updateAll(original)
    }

    suspend fun delete(card: Card) {
        cardDao.delete(card)
    }
}