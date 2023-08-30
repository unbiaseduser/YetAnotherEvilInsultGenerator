package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult

fun List<Insult>.sortAndFilter(
    sortOptions: InsultSortOptions?,
    filterOptions: InsultFilterOptions?
): List<Insult> {
    var results = this
    if (filterOptions != null) {
        results = filterOptions.filterInsults(results)
    }
    if (sortOptions != null) {
        if (sortOptions.mode != null) {
            results = results.sortedWith(sortOptions.mode.insultComparator)
        }
        if (sortOptions.favoritesFirst) {
            results = results.sortedWith { first, second ->
                val isFirstFavorited = first.isFavorite
                val isSecondFavorited = second.isFavorite
                return@sortedWith when {
                    !isFirstFavorited && isSecondFavorited -> 1
                    isFirstFavorited && !isSecondFavorited -> -1
                    else -> 0
                }
            }
        }
    }
    return results
}