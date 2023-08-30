package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.view.View
import com.kennyc.view.MultiStateView

fun MultiStateView.setContentView(view: View, switchToState: Boolean = false) = setViewForState(view, MultiStateView.ViewState.CONTENT, switchToState)

fun MultiStateView.setEmptyView(view: View, switchToState: Boolean = false) = setViewForState(view, MultiStateView.ViewState.EMPTY, switchToState)

fun MultiStateView.setErrorView(view: View, switchToState: Boolean = false) = setViewForState(view, MultiStateView.ViewState.ERROR, switchToState)
