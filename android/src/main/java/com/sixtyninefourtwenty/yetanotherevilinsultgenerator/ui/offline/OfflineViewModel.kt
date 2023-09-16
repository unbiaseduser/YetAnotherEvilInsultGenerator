package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.offline

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.sortAndFilter
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.getInsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.getInsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setInsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setInsultSortOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineViewModel(
    private val repository: InsultsRepository,
    private val ssh: SavedStateHandle
) : ViewModel() {

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

    @set:MainThread //required by LiveData.setValue()
    var insultFilterOptions: InsultFilterOptions?
        get() = mutableInsultFilterOptionsLiveData.value
        set(value) {
            mutableInsultFilterOptionsLiveData.value = value
            ssh.setInsultFilterOptions(INSULT_FILTER_OPTIONS_STATE_KEY, value)
        }

    @set:MainThread
    var insultSortOptions: InsultSortOptions?
        get() = mutableInsultSortOptionsLiveData.value
        set(value) {
            mutableInsultSortOptionsLiveData.value = value
            ssh.setInsultSortOptions(INSULT_SORT_OPTIONS_STATE_KEY, value)
        }

    fun deleteLocalInsults(insults: Iterable<Insult>, blockAfter: (() -> Unit)? = null) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.deleteLocalInsults(insults)
        }
        blockAfter?.invoke()
    }

    init {
        insultFilterOptions = ssh.getInsultFilterOptions(INSULT_FILTER_OPTIONS_STATE_KEY)
        insultSortOptions = ssh.getInsultSortOptions(INSULT_SORT_OPTIONS_STATE_KEY)
    }

    companion object {
        private const val INSULT_FILTER_OPTIONS_STATE_KEY = "ifo"
        private const val INSULT_SORT_OPTIONS_STATE_KEY = "iso"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (get(APPLICATION_KEY) as MyApplication).insultsRepository
                OfflineViewModel(repo, createSavedStateHandle())
            }
        }
    }
}