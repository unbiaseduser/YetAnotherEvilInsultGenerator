package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import java.util.Locale
import java.util.ResourceBundle

fun resourceBundle(fileName: String, locale: Locale = Locale.getDefault()): ResourceBundle =
    ResourceBundle.getBundle("strings.$fileName.$fileName", locale)

fun Language.getLabel(bundle: ResourceBundle): String = bundle.getString(languageCode)
