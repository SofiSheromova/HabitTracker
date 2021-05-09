package com.example.habittracker.model

enum class Priority(val value: Int) {
    LOW(0),
    MIDDLE(1),
    HIGH(2);

    companion object {
        fun valueOf(value: Int): Priority = values().find { it.value == value } ?: LOW
    }
}
