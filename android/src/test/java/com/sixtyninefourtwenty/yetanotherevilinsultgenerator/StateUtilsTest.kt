package com.sixtyninefourtwenty.yetanotherevilinsultgenerator

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.TextFilter
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.getInsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.getInsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setInsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setInsultSortOptions
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private const val KEY = "opt"

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class StateUtilsTest {

    private lateinit var ssh: SavedStateHandle

    @Before
    fun init() {
        ssh = SavedStateHandle()
    }

    @Test
    fun persistInsultFilterOptions() {
        persistInsultFilterOption(null)
        persistInsultFilterOption(
            InsultFilterOptions(
                language = null,
                textFilter = null
            )
        )
        persistInsultFilterOption(
            InsultFilterOptions(
                language = Language.ENGLISH,
                textFilter = null
            )
        )
        persistInsultFilterOption(
            InsultFilterOptions(
                language = null,
                textFilter = TextFilter(
                    textToSearch = "something",
                    mode = TextFilter.Mode.CONTAINS
                )
            )
        )
        persistInsultFilterOption(
            InsultFilterOptions(
                language = Language.ENGLISH,
                textFilter = TextFilter(
                    textToSearch = "something",
                    mode = TextFilter.Mode.CONTAINS
                )
            )
        )
        persistInsultFilterOption(
            InsultFilterOptions(
                language = null,
                textFilter = TextFilter(
                    textToSearch = null,
                    mode = TextFilter.Mode.CONTAINS
                )
            )
        )
        persistInsultFilterOption(
            InsultFilterOptions(
                language = Language.ENGLISH,
                textFilter = TextFilter(
                    textToSearch = null,
                    mode = TextFilter.Mode.CONTAINS
                )
            )
        )
    }

    private fun persistInsultFilterOption(options: InsultFilterOptions?) {
        ssh.setInsultFilterOptions(KEY, options)
        assertEquals(options, ssh.getInsultFilterOptions(KEY))
    }

    @Test
    fun persistInsultSortOptions() {
        persistInsultSortOptions(null)
        persistInsultSortOptions(
            InsultSortOptions(
                favoritesFirst = false,
                mode = null
            )
        )
        persistInsultSortOptions(
            InsultSortOptions(
                favoritesFirst = false,
                mode = InsultSortOptions.Mode.CONTENT_ALPHABETICAL_ASCENDING
            )
        )
    }

    private fun persistInsultSortOptions(options: InsultSortOptions?) {
        ssh.setInsultSortOptions(KEY, options)
        assertEquals(options, ssh.getInsultSortOptions(KEY))
    }

}