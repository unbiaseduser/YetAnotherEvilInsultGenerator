package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(parameter = 0)
fun isDeviceOnOrOverSdk(sdkInt: Int) = Build.VERSION.SDK_INT >= sdkInt

fun isDeviceOnOrUnderSdk(sdkInt: Int) = Build.VERSION.SDK_INT <= sdkInt
