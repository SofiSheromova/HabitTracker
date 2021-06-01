package com.example.domain.model

data class Periodicity(val repetitionsNumber: Int, val daysNumber: Int) {

    constructor() : this(1, 1)
}