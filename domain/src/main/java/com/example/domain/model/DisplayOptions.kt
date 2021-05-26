package com.example.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DisplayOptions(
    vararg localFilters: (Habit) -> Boolean
) {
    private val filters: MutableList<(Habit) -> Boolean> = localFilters.toMutableList()

    fun filter(habits: List<Habit>?): List<Habit> {
        return (habits ?: listOf()).sort().searchBarFilter().localFilter()
    }

    fun filter(habits: Flow<List<Habit>>): Flow<List<Habit>> {
        return habits.map { it.sort().searchBarFilter().localFilter() }
    }

    private fun List<Habit>.localFilter(): List<Habit> {
        var newValue = this
        filters.forEach { filter ->
            newValue = newValue.filter(filter)
        }
        return newValue
    }

    // TODO сказали, что не очень прикольно иметь статические поля и лучше это куда-то в даггер день
    companion object {
        var searchBarData: String? = null
        var sortedFunction: ((List<Habit>) -> List<Habit>)? = null

        fun sortByTitle(reverse: Boolean = false) {
            sortedFunction = if (reverse) {
                { cards -> cards.sortedBy { it.title }.asReversed() }
            } else {
                { cards -> cards.sortedBy { it.title } }
            }
        }

        private fun List<Habit>.searchBarFilter(): List<Habit> {
            searchBarData?.let { request ->
                return this.filter { it.title.contains(request) }
            }
            return this
        }

        private fun List<Habit>.sort(): List<Habit> {
            sortedFunction?.let {
                return it(this)
            }
            return this
        }
    }
}