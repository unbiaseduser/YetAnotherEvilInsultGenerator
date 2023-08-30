package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MainActivity
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.MyApplication
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.PreferencesRepository

fun Fragment.preferenceRepository(): Lazy<PreferencesRepository> = lazy {
    (requireActivity().application as MyApplication).preferencesRepository
}

fun Fragment.addMenuProvider(menuProvider: MenuProvider, lifecycleOwner: LifecycleOwner = viewLifecycleOwner) = requireActivity().addMenuProvider(menuProvider, lifecycleOwner)

val Fragment.actionMode get() = (requireActivity() as MainActivity).actionMode

val Fragment.myApplication get() = requireActivity().application as MyApplication

fun Fragment.startActivitySafely(intent: Intent, exceptionHandler: (ActivityNotFoundException) -> Unit) = requireContext().startActivitySafely(intent, exceptionHandler)
