package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.tint(color: Int) = DrawableCompat.wrap(mutate()).also { DrawableCompat.setTint(it, color) }
