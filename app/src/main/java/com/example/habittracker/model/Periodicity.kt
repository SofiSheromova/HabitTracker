package com.example.habittracker.model

class Periodicity(_repetitionsNumber: Int, _daysNumber: Int) {

    constructor() : this(1, 1)

    // TODO пришлось сделать var из-за ошибок с базой данных
    var repetitionsNumber: Int
    var daysNumber: Int

    init {
        val gcd = getGCD(_repetitionsNumber, _daysNumber)
        repetitionsNumber = _repetitionsNumber / gcd
        daysNumber = _daysNumber / gcd
    }

    private fun getGCD(first: Int, second: Int): Int {
        var n1 = first
        var n2 = second
        while (n1 != n2) {
            if (n1 > n2)
                n1 -= n2
            else
                n2 -= n1
        }
        return n1
    }
}