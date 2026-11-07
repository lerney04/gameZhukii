package com.example.gamezhukii

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Locale

class GoldWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        renderAll(context)
        WorkManager.getInstance(context).enqueueUniqueWork(
            "refresh_gold_quote", ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<GoldQuoteWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
        )
    }

    companion object {
        fun renderAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val quote = GoldPriceRepository.cached(context)
            val views = RemoteViews(context.packageName, R.layout.widget_gold)
            views.setTextViewText(R.id.widgetGoldPrice, quote?.let {
                String.format(Locale.getDefault(), "%,.2f ₽/г", it.rublesPerGram)
            } ?: context.getString(R.string.gold_no_data))
            views.setTextViewText(R.id.widgetGoldDate, quote?.date ?: context.getString(R.string.gold_waiting))
            val update = Intent(context, GoldWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    manager.getAppWidgetIds(ComponentName(context, GoldWidgetProvider::class.java)))
            }
            views.setOnClickPendingIntent(R.id.widgetGoldBall, PendingIntent.getBroadcast(
                context, 0, update, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))
            manager.getAppWidgetIds(ComponentName(context, GoldWidgetProvider::class.java))
                .forEach { manager.updateAppWidget(it, views) }
        }
    }
}

class GoldQuoteWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    override fun doWork(): Result {
        val result = runCatching { GoldPriceRepository.fetch(applicationContext) }
        result.exceptionOrNull()?.let { Log.w("GoldQuote", "Widget refresh failed", it) }
        GoldWidgetProvider.renderAll(applicationContext)
        return if (result.isSuccess) Result.success() else Result.retry()
    }
}
