package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.insultinfo

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsultInfoViewModel(private val repository: InsultsRepository) : ViewModel() {

    private val currentlyDisplayedInsultLiveData = MutableLiveData<Insult>()
    fun setCurrentlyDisplayedInsult(insult: Insult) {
        currentlyDisplayedInsultLiveData.value = insult
    }

    fun updateFavoriteStatus(insult: Insult, isFavorite: Boolean, blockAfter: (() -> Unit)? = null) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.updateFavoriteStatus(insult, isFavorite)
        }
        blockAfter?.invoke()
    }

    fun getFavoriteStatus(insult: Insult) = repository.getFavoriteStatus(insult)

    fun deleteLocalInsult(insult: Insult, blockAfter: (() -> Unit)? = null) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.deleteLocalInsult(insult)
        }
        blockAfter?.invoke()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (get(APPLICATION_KEY) as MyApplication).insultsRepository
                InsultInfoViewModel(repo)
            }
        }
    }
}