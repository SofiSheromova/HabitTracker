package com.example.domain.model

enum class Type(val value: Int) {
    GOOD(0),
    BAD(1);

    companion object {
        fun valueOf(value: Int): Type = values().find { it.value == value } ?: GOOD
    }
}