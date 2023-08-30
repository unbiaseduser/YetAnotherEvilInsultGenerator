package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.insultoftheday

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MainActivity
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.AbstractInsultGenerationWorker
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_GENERATION_PROGRESS_NOTIFICATION_ID
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_NOTIFICATION_ACTION_SHOW_INFO_ID
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_NOTIFICATION_CHANNEL_ID
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_NOTIFICATION_ID
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.canAppSendIotdNotifications
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.myApplication

class InsultOfTheDayWorker(
    appContext: Context,
    params: WorkerParameters
) : AbstractInsultGenerationWorker(appContext, params) {

    @SuppressLint("MissingPermission")
    override suspend fun handleInsult(insult: Insult) {
        applicationContext.myApplication.insultsRepository.addOrUpdateLocalInsult(insult)
        if (AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, InsultOfTheDayAppWidgetProvider::class.java)).isNotEmpty()) {
            applicationContext.sendBroadcast(
                InsultOfTheDayAppWidgetProvider.createRealUpdateIntent(
                    applicationContext,
                    insult
                )
            )
        }
        if (applicationContext.canAppSendIotdNotifications()) {
            NotificationManagerCompat.from(applicationContext)
                .notify(
                    IOTD_NOTIFICATION_ID,
                    NotificationCompat.Builder(applicationContext, IOTD_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(applicationContext.getString(R.string.insult_of_the_day))
                        .setStyle(NotificationCompat.BigTextStyle().bigText(insult.insult))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(
                            PendingIntent.getActivity(
                                applicationContext,
                                IOTD_NOTIFICATION_ACTION_SHOW_INFO_ID,
                                MainActivity.createActionShowIotdIntent(applicationContext, insult),
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                        .addAction(
                            R.drawable.info,
                            applicationContext.getString(R.string.info),
                            PendingIntent.getActivity(
                                applicationContext,
                                IOTD_NOTIFICATION_ACTION_SHOW_INFO_ID,
                                MainActivity.createActionShowIotdIntent(applicationContext, insult),
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                        .build()
                )
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            IOTD_GENERATION_PROGRESS_NOTIFICATION_ID,
            NotificationCompat.Builder(applicationContext, IOTD_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(applicationContext.getString(R.string.insult_of_the_day))
                .setContentText(applicationContext.getString(R.string.generating_insult))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .build()
        )
    }

    companion object {
        const val UNIQUE_WORK_KEY = "iotd_work"
    }

}