package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.offline

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.sortAndFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineViewModel(private val repository: InsultsRepository) : ViewModel() {

    private val allAvailableInsultsLiveData = repository.allAvailableLocalInsults.asLiveData()
    private val mutableInsultFilterOptionsLiveData = MutableLiveData<InsultFilterOptions?>()
    private val mutableInsultSortOptionsLiveData = MutableLiveData<InsultSortOptions?>()
    private val insultsAfterSearchAndFilterMediatorLiveData = MediatorLiveData<List<Insult>>().apply {
        addSource(allAvailableInsultsLiveData) { insults ->
            value = insults.sortAndFilter(mutableInsultSortOptionsLiveData.value, mutableInsultFilterOptionsLiveData.value)
        }
        addSource(mutableInsultFilterOptionsLiveData) {
            val insults = allAvailableInsultsLiveData.value
            value = insults?.sortAndFilter(mutableInsultSortOptionsLiveData.value, it) ?: listOf()
        }
        addSource(mutableInsultSortOptionsLiveData) {
            val insults = allAvailableInsultsLiveData.value
            value = insults?.sortAndFilter(it, mutableInsultFilterOptionsLiveData.value) ?: listOf()
        }
    }
    val insultsAfterSearchAndFilter: LiveData<List<Insult>> = insultsAfterSearchAndFilterMediatorLiveData

    @set:MainThread //LiveData.setValue()
    var insultFilterOptions: InsultFilterOptions? by mutableInsultFilterOptionsLiveData::value

    @set:MainThread
    var insultSortOptions: InsultSortOptions? by mutableInsultSortOptionsLiveData::value

    fun deleteLocalInsults(insults: Iterable<Insult>, blockAfter: (() -> Unit)? = null) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.deleteLocalInsults(insults)
        }
        blockAfter?.invoke()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (get(APPLICATION_KEY) as MyApplication).insultsRepository
                OfflineViewModel(repo)
            }
        }
    }
}