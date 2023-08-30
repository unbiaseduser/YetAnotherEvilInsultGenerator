package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.insultoftheday

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.BuildConfig
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.createWorkManagerConstraints
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.alarmManager
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.myApplication
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime

class InsultOfTheDayBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != ACTION_IOTD) {
            return
        }
        val constraints = context.myApplication.preferencesRepository.iotdWorkManagerConstraints
        WorkManager.getInstance(context).enqueueUniqueWork(
            InsultOfTheDayWorker.UNIQUE_WORK_KEY, ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<InsultOfTheDayWorker>()
            .setConstraints(constraints.createWorkManagerConstraints())
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        )
    }

    companion object {
        const val ACTION_IOTD = "${BuildConfig.APPLICATION_ID}.ACTION_IOTD"
        private fun createPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context,
            69,
            Intent(ACTION_IOTD).setClass(context, InsultOfTheDayBroadcastReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        fun createCancelPendingIntent(context: Context): PendingIntent? = PendingIntent.getBroadcast(
            context,
            69,
            Intent(ACTION_IOTD).setClass(context, InsultOfTheDayBroadcastReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
        fun registerDailyInvocation(context: Context, time: LocalTime) {
            context.alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                ZonedDateTime.now().withHour(time.hour).withMinute(time.minute).toInstant().toEpochMilli(),
                Duration.ofDays(1).toMillis(),
                createPendingIntent(context)
            )
        }
        fun cancelDailyInvocation(context: Context) {
            context.alarmManager.cancel(createPendingIntent(context))
        }
    }

}