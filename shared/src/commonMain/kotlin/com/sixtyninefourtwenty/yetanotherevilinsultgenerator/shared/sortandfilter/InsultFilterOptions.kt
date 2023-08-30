package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult

data class InsultFilterOptions(
    val language: Language?,
    val textFilter: TextFilter?
) {

    fun filterInsults(insults: List<Insult>): List<Insult> {
        var results = insults
        if (language != null) {
            results = results.filter { it.getLanguage() == language }
        }
        if (textFilter != null) {
            results = results.filter { textFilter.testText(it.insult) }
        }
        return results
    }

}