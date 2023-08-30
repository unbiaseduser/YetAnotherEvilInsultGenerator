package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.offline

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository

class OfflineViewModel(private val repository: InsultsRepository) {

    val allAvailableInsults = repository.allAvailableLocalInsults

    suspend fun deleteLocalInsults(insults: Iterable<Insult>) {
        repository.deleteLocalInsults(insults)
    }

}