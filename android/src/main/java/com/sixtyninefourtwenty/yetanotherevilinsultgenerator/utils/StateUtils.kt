package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.TextFilter

fun SavedStateHandle.setInsultFilterOptions(key: String, options: InsultFilterOptions?) {
    set(key, options?.let { opt ->
        Bundle().apply {
            putString("lang", opt.language?.name)
            putBundle("text_filter", opt.textFilter?.let { textFilter ->
                Bundle().apply {
                    putString("text", textFilter.textToSearch)
                    putString("mode", textFilter.mode.name)
                }
            })
        }
    })
}

fun SavedStateHandle.getInsultFilterOptions(key: String) = get<Bundle>(key)?.let { outer ->
    InsultFilterOptions(
        language = outer.getString("lang")?.let { Language.valueOf(it) },
        textFilter = outer.getBundle("text_filter")?.let { inner ->
            TextFilter(
                textToSearch = inner.getString("text"),
                mode = TextFilter.Mode.valueOf(inner.getString("mode")!!)
            )
        }
    )
}

fun SavedStateHandle.setInsultSortOptions(key: String, options: InsultSortOptions?) {
    set(key, options?.let { opt ->
        Bundle().apply {
            putBoolean("fav_first", opt.favoritesFirst)
            putString("mode", opt.mode?.name)
        }
    })
}

fun SavedStateHandle.getInsultSortOptions(key: String) = get<Bundle>(key)?.let { bundle ->
    InsultSortOptions(
        favoritesFirst = bundle.getBoolean("fav_first"),
        mode = bundle.getString("mode")?.let { InsultSortOptions.Mode.valueOf(it) }
    )
}
