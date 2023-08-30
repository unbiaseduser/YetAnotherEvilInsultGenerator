package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication

/**
 * Construct a [RemoteViews] with the context's package name
 */
fun Context.createRemoteViews(@LayoutRes layoutRes: Int) = RemoteViews(packageName, layoutRes)

fun Context.canAppSendIotdNotifications(): Boolean {
    return when {
        isDeviceOnOrUnderSdk(Build.VERSION_CODES.N) -> true
        isDeviceOnOrOverSdk(Build.VERSION_CODES.O) && hasNotificationChannel(IOTD_NOTIFICATION_CHANNEL_ID) -> true
        isDeviceOnOrOverSdk(Build.VERSION_CODES.TIRAMISU) &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                hasNotificationChannel(IOTD_NOTIFICATION_CHANNEL_ID) -> true
        else -> false
    }
}

//purposely use the android framework class to enforce the sdk 26 check
@RequiresApi(Build.VERSION_CODES.O)
fun Context.hasNotificationChannel(channelId: String) = ((getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager).getNotificationChannel(channelId) != null

val Context.alarmManager get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.clipboardManager get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

val Context.myApplication get() = if (this is MyApplication) this else applicationContext as MyApplication

fun Context.startActivitySafely(intent: Intent, exceptionHandler: (ActivityNotFoundException) -> Unit) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        exceptionHandler(e)
    }
}
