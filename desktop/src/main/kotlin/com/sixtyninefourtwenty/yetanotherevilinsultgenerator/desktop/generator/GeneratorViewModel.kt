package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.generator

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.ViewState
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.utils.asExceptionOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class GeneratorViewModel(private val mainInsultsRepository: InsultsRepository,
                         private val backupInsultsRepository: InsultsRepository
) {

    private val mutableViewState = MutableStateFlow<ViewState<Insult>>(ViewState.Empty)
    val viewState = mutableViewState.asStateFlow()

    suspend fun generateRemoteInsult(language: Language, generatorURL: OfficialOnlineInsultGeneratorService.Url) = withContext(Dispatchers.IO) {
        mutableViewState.value = ViewState.Loading
        runCatching {
            when (generatorURL) {
                OfficialOnlineInsultGeneratorService.Url.MAIN -> mainInsultsRepository.generateRemoteInsult(language)
                OfficialOnlineInsultGeneratorService.Url.BACKUP -> backupInsultsRepository.generateRemoteInsult(language)
            }
        }.onFailure {
            mutableViewState.value = ViewState.Error(it.asExceptionOrThrow())
        }.onSuccess {
            addOrUpdateLocalInsult(it)
            mutableViewState.value = ViewState.Content(it)
        }
    }

    suspend fun addOrUpdateLocalInsult(insult: Insult) = withContext(Dispatchers.IO) {
        mainInsultsRepository.addOrUpdateLocalInsult(insult)
    }

}