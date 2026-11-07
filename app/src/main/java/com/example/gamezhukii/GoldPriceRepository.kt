package com.example.gamezhukii

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface CbrApi {
    @GET("scripts/xml_metall.asp")
    fun goldPrices(@Query("date_req1") from: String, @Query("date_req2") to: String): Call<ResponseBody>
}

object GoldPriceRepository {
    private val executor = Executors.newSingleThreadExecutor()
    private val api = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/")
        .client(OkHttpClient.Builder().callTimeout(25, TimeUnit.SECONDS).build())
        .build().create(CbrApi::class.java)

    fun cached(context: Context): GoldQuote? {
        val prefs = context.getSharedPreferences("gold_quote", Context.MODE_PRIVATE)
        val price = prefs.getString("price", null)?.toDoubleOrNull() ?: return null
        val date = prefs.getString("date", null) ?: return null
        return if (price.isFinite() && price > 0) GoldQuote(price, date) else null
    }

    /** Called only by a Worker or our background executor. */
    fun fetch(context: Context): GoldQuote {
        val today = Calendar.getInstance()
        val from = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -30) }
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        val response = api.goldPrices(format.format(from.time), format.format(today.time)).execute()
        if (!response.isSuccessful) {
            response.errorBody()?.close()
            error("CBR HTTP ${response.code()}")
        }
        val quote = requireNotNull(response.body()).use { body ->
            body.byteStream().use(GoldQuoteParser::parse)
        }
        val saved = context.getSharedPreferences("gold_quote", Context.MODE_PRIVATE).edit()
            .putString("price", quote.rublesPerGram.toString())
            .putString("date", quote.date).commit()
        check(saved) { "Cannot persist CBR quote" }
        return quote
    }

    fun refresh(context: Context, done: (GoldQuote?) -> Unit) {
        val app = context.applicationContext
        executor.execute {
            val quote = runCatching { fetch(app) }.getOrElse {
                Log.w("GoldQuote", "Cannot load CBR gold quote", it)
                cached(app)
            }
            GoldWidgetProvider.renderAll(app)
            done(quote)
        }
    }
}
