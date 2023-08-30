package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.insultoftheday

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.BuildConfig
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_EXTRA_KEY
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.createRemoteViews
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.myApplication

class InsultOfTheDayAppWidgetProvider : AppWidgetProvider() {

    private fun createRemoteViews(context: Context) = context.createRemoteViews(R.layout.widget_iotd)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.v(javaClass.simpleName, "onUpdate")
        val insult = context.myApplication.preferencesRepository.savedIotdInsult
        for (id in appWidgetIds) {
            val remoteViews = createRemoteViews(context)
            if (insult != null) {
                remoteViews.setTextViewText(R.id.insult, insult.insult)
            } else {
                remoteViews.setViewVisibility(R.id.iotd_title, View.GONE)
            }
            appWidgetManager.updateAppWidget(id, remoteViews)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_IOTD_WIDGET) {
            Log.v(javaClass.simpleName, "onReceive (iotd)")
            context.myApplication.preferencesRepository.savedIotdInsult = Insult.fromJson(intent.getStringExtra(IOTD_EXTRA_KEY)!!)
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_IOTD_WIDGET = "${BuildConfig.APPLICATION_ID}.ACTION_IOTD_WIDGET"
        fun createRealUpdateIntent(context: Context, insult: Insult) = Intent(context, InsultOfTheDayAppWidgetProvider::class.java)
            .setAction(ACTION_IOTD_WIDGET)
            .putExtra(IOTD_EXTRA_KEY, insult.toJson())
    }
}