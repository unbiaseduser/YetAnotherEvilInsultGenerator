package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MainViewModel {

    private val mutableScreenFlow = MutableStateFlow(Screen.GENERATOR).apply {
        distinctUntilChanged(Any::equals)
    }

    val screenFlow = mutableScreenFlow.asStateFlow()

    var screen: Screen by mutableScreenFlow::value

    enum class Screen {
        GENERATOR, OFFLINE
    }

}