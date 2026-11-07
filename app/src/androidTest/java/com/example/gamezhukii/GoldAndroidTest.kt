package com.example.gamezhukii

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoldAndroidTest {
    @Test
    fun parsesCbrXmlOnAndroid() {
        val xml = """<Metall><Record Date="10.11.2026" Code="1"><Buy>10774,32</Buy></Record></Metall>"""
        assertEquals(108, GoldQuoteParser.parse(xml.byteInputStream()).reward)
    }
}
