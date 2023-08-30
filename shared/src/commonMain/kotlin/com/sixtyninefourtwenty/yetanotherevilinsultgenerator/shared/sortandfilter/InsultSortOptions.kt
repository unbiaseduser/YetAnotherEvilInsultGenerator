package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult

data class InsultSortOptions(
    val favoritesFirst: Boolean,
    val mode: Mode?
) {
    enum class Mode(val insultComparator: Comparator<Insult>) {
        CONTENT_ALPHABETICAL_ASCENDING({ first, second ->
            first.insult.lowercase().compareTo(second.insult.lowercase())
        }),
        CONTENT_ALPHABETICAL_DESCENDING({ first, second ->
            second.insult.lowercase().compareTo(first.insult.lowercase())
        })
    }
}
