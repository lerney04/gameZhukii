package com.example.gamezhukii

import org.junit.Assert.assertEquals
import org.junit.Test

class ZodiacCalculatorTest {
    @Test
    fun calculatesSignsOnBoundaryDates() {
        assertEquals("Козерог", ZodiacCalculator.calculate(1, 19))
        assertEquals("Водолей", ZodiacCalculator.calculate(1, 20))
        assertEquals("Рыбы", ZodiacCalculator.calculate(2, 19))
        assertEquals("Овен", ZodiacCalculator.calculate(3, 21))
        assertEquals("Телец", ZodiacCalculator.calculate(4, 20))
        assertEquals("Близнецы", ZodiacCalculator.calculate(5, 21))
        assertEquals("Рак", ZodiacCalculator.calculate(6, 21))
        assertEquals("Лев", ZodiacCalculator.calculate(7, 23))
        assertEquals("Дева", ZodiacCalculator.calculate(8, 23))
        assertEquals("Весы", ZodiacCalculator.calculate(9, 23))
        assertEquals("Скорпион", ZodiacCalculator.calculate(10, 23))
        assertEquals("Стрелец", ZodiacCalculator.calculate(11, 22))
        assertEquals("Козерог", ZodiacCalculator.calculate(12, 22))
    }
}
