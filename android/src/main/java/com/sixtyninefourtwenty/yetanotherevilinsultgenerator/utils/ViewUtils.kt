package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.LocalTime

fun AppCompatEditText.isInputEmpty() = text == null || text.toString().isBlank()

fun AppCompatEditText.getInput() = if (isInputEmpty()) "" else text.toString().trim()

var ImageView.drawableContent: Drawable?
    get() = drawable
    set(value) = setImageDrawable(value)
