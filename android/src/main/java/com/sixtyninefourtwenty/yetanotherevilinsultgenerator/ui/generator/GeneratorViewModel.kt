package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.ViewState
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.utils.asExceptionOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeneratorViewModel(
    private val mainInsultsRepository: InsultsRepository,
    private val backupInsultsRepository: InsultsRepository
) : ViewModel() {

    private val mutableViewState = MutableStateFlow<ViewState<Insult>>(ViewState.Empty)
    val viewState = mutableViewState.asStateFlow()

    fun generateRemoteInsult(
        language: Language,
        generatorURL: OfficialOnlineInsultGeneratorService.Url
    ) = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.value = ViewState.Loading
        runCatching {
            when (generatorURL) {
                OfficialOnlineInsultGeneratorService.Url.MAIN -> mainInsultsRepository.generateRemoteInsult(
                    language
                )

                OfficialOnlineInsultGeneratorService.Url.BACKUP -> backupInsultsRepository.generateRemoteInsult(
                    language
                )
            }
        }.onFailure {
            mutableViewState.value = ViewState.Error(it.asExceptionOrThrow())
        }.onSuccess {
            addOrUpdateLocalInsult(it)
            mutableViewState.value = ViewState.Content(it)
        }
    }

    fun addOrUpdateLocalInsult(insult: Insult) = viewModelScope.launch(Dispatchers.IO) {
        mainInsultsRepository.addOrUpdateLocalInsult(insult)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = get(APPLICATION_KEY) as MyApplication
                GeneratorViewModel(app.insultsRepository, app.backupInsultsRepository)
            }
        }
    }
}