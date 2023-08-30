package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language.*
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult

fun Insult.share(context: Context) {
    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND)
        .putExtra(Intent.EXTRA_TEXT, insult)
        .setType("text/plain"), null))
}

fun Insult.copyToClipboard(context: Context) {
    context.clipboardManager.setPrimaryClip(ClipData.newPlainText("insult", insult))
}

val OfficialOnlineInsultGeneratorService.Url.prefValue: String
    get() {
        return when (this) {
            OfficialOnlineInsultGeneratorService.Url.MAIN -> "main"
            OfficialOnlineInsultGeneratorService.Url.BACKUP -> "backup"
        }
    }

@get:StringRes
val Language.languageLabelResId: Int get() {
    return when (this) {
        ENGLISH -> R.string.english
        SPANISH -> R.string.spanish
        CHINESE -> R.string.chinese
        HINDI -> R.string.hindi
        ARABIC -> R.string.arabic
        PORTUGUESE -> R.string.portuguese
        BENGALI -> R.string.bengali
        RUSSIAN -> R.string.russian
        JAPANESE -> R.string.japanese
        JAVANESE -> R.string.javanese
        SWAHILI -> R.string.swahili
        GERMAN -> R.string.german
        KOREAN -> R.string.korean
        FRENCH -> R.string.french
        TELUGU -> R.string.telugu
        MARATHI -> R.string.marathi
        TURKISH -> R.string.turkish
        TAMIL -> R.string.tamil
        VIETNAMESE -> R.string.vietnamese
        URDU -> R.string.urdu
        GREEK -> R.string.greek
        ITALIAN -> R.string.italian
        CZECH -> R.string.czech
        FINNISH -> R.string.finnish
        AFRIKAANS -> R.string.afrikaans
        BULGARIAN -> R.string.bulgarian
        CATALAN -> R.string.catalan
        DANISH -> R.string.danish
        DUTCH -> R.string.dutch
        HEBREW -> R.string.hebrew
        HUNGARIAN -> R.string.hungarian
        INDONESIAN -> R.string.indonesian
        KANNADA -> R.string.kannada
        NORWEGIAN -> R.string.norwegian
        PERSIAN -> R.string.persian
        POLISH -> R.string.polish
        PUNJABI -> R.string.punjabi
        ROMANIAN -> R.string.romanian
        SERBIAN -> R.string.serbian
        SINHALA -> R.string.sinhala
        SWEDISH -> R.string.swedish
        THAI -> R.string.thai
        UKRAINIAN -> R.string.ukrainian
        LATIN -> R.string.latin
    }
}
