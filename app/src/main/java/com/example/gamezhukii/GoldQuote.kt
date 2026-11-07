package com.example.gamezhukii

import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import kotlin.math.roundToInt

data class GoldQuote(val rublesPerGram: Double, val date: String) {
    val reward: Int get() = (rublesPerGram / 100.0).roundToInt().coerceAtLeast(1)
}

object GoldQuoteParser {
    fun parse(stream: InputStream): GoldQuote {
        val parser = XmlPullParserFactory.newInstance().newPullParser().apply {
            require(!getFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL)) { "DTD processing must be disabled" }
            setInput(stream, null)
        }
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).apply { isLenient = false }
        var latest: GoldQuote? = null
        var latestTime = Long.MIN_VALUE
        var isGold = false
        var date = ""
        var price: Double? = null
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            require(event != XmlPullParser.DOCDECL) { "DTD is not allowed" }
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "Record" -> {
                        isGold = parser.getAttributeValue(null, "Code") == "1"
                        date = parser.getAttributeValue(null, "Date") ?: ""
                        price = null
                    }
                    "Buy" -> if (isGold) {
                        price = parser.nextText().replace("\u00a0", "")
                            .replace(" ", "").replace(',', '.').toDoubleOrNull()
                    }
                }
            } else if (event == XmlPullParser.END_TAG && parser.name == "Record" && isGold) {
                val time = runCatching { format.parse(date)?.time }.getOrNull()
                val value = price
                if (time != null && value != null && value.isFinite() && value > 0 && time > latestTime) {
                    latest = GoldQuote(value, date)
                    latestTime = time
                }
                isGold = false
            }
            event = parser.nextToken()
        }
        return requireNotNull(latest) { "No valid gold quote in CBR response" }
    }
}
