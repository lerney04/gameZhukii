package com.example.gamezhukii

object ZodiacCalculator {
    fun calculate(month: Int, day: Int): String = when {
        month == 1 && day >= 20 || month == 2 && day <= 18 -> "Водолей"
        month == 2 || month == 3 && day <= 20 -> "Рыбы"
        month == 3 || month == 4 && day <= 19 -> "Овен"
        month == 4 || month == 5 && day <= 20 -> "Телец"
        month == 5 || month == 6 && day <= 20 -> "Близнецы"
        month == 6 || month == 7 && day <= 22 -> "Рак"
        month == 7 || month == 8 && day <= 22 -> "Лев"
        month == 8 || month == 9 && day <= 22 -> "Дева"
        month == 9 || month == 10 && day <= 22 -> "Весы"
        month == 10 || month == 11 && day <= 21 -> "Скорпион"
        month == 11 || month == 12 && day <= 21 -> "Стрелец"
        else -> "Козерог"
    }
}
