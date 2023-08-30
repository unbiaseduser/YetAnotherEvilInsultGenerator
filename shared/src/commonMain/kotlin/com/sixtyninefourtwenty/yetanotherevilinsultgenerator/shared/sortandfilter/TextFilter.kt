package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter

data class TextFilter(
    val textToSearch: String?,
    val mode: Mode
) {

    fun testText(text: String): Boolean {
        if (textToSearch.isNullOrBlank()) {
            return true
        }
        return mode.testText(text, textToSearch)
    }

    enum class Mode {
        CONTAINS, NOT_CONTAINS;

        fun testText(text: String, textToSearch: String): Boolean {
            return when (this) {
                CONTAINS -> text.contains(textToSearch, ignoreCase = true)
                NOT_CONTAINS -> !text.contains(textToSearch, ignoreCase = true)
            }
        }
    }

}