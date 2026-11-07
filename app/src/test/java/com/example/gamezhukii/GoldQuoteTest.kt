package com.example.gamezhukii

import org.junit.Assert.assertEquals
import org.junit.Test

class GoldQuoteTest {
    @Test(expected = IllegalArgumentException::class)
    fun rejectsDtdInsteadOfExpandingEntities() {
        GoldQuoteParser.parse("""<!DOCTYPE Metall [<!ENTITY price "12345">]>
            <Metall><Record Date="10.11.2026" Code="1"><Buy>&price;</Buy></Record></Metall>""".byteInputStream())
    }
    @Test
    fun selectsLatestGoldRecordRegardlessOfOrderAndIgnoresOtherMetals() {
        val xml = """<Metall>
            <Record Date="10.11.2026" Code="1"><Buy>10774,32</Buy></Record>
            <Record Date="11.11.2026" Code="2"><Buy>140,5</Buy></Record>
            <Record Date="07.11.2026" Code="1"><Buy>10861,28</Buy></Record>
        </Metall>"""
        val quote = GoldQuoteParser.parse(xml.byteInputStream())
        assertEquals(10774.32, quote.rublesPerGram, 0.001)
        assertEquals("10.11.2026", quote.date)
        assertEquals(108, quote.reward)
    }

    @Test(expected = IllegalArgumentException::class)
    fun doesNotInventGoldPriceWhenResponseHasNoGold() {
        GoldQuoteParser.parse("<Metall/>".byteInputStream())
    }
}
