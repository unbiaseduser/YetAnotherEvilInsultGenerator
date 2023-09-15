package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sixtyninefourtwenty.custompreferences.PreferenceFragmentCompatAccommodateCustomDialogPreferences
import com.sixtyninefourtwenty.custompreferences.TimePickerPreference
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.PeriodicInsultUpdatesWorker
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.PreferencesRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.createWorkManagerConstraints
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.insultoftheday.InsultOfTheDayBroadcastReceiver
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.*
import java.time.LocalTime

class SettingsFragment : PreferenceFragmentCompatAccommodateCustomDialogPreferences() {

    private lateinit var prefs: PreferencesRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        prefs = requireContext().myApplication.preferencesRepository
        setPreferencesFromResource(R.xml.preferences_root, rootKey)
        setupNavigateToAppearancePreferences()
        setupGeneratorLanguagePreference()
        setupEIGStatusPagePreference()
        setupInsultOfTheDayPreferences()
        setupSchedulePeriodicInsultUpdatesPreferences()
    }

    private fun setupNavigateToAppearancePreferences() {
        with(findPreference<Preference>("navigation_appearance_prefs")!!) {
            setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationAppearanceSettings())
                true
            }
        }
    }

    private fun setupGeneratorLanguagePreference() {
        with(findPreference<ListPreference>("generator_language")!!) {
            val languages = Language.values()
            entries = languages.map { it.languageLabelResId }.map(::getString).toTypedArray()
            entryValues = languages.map { it.languageCode }.toTypedArray()
        }
    }

    private fun setupEIGStatusPagePreference() {
        with(findPreference<Preference>("eig_status_page")!!) {
            setOnPreferenceClickListener {
                startActivitySafely(Intent(Intent.ACTION_VIEW).setData("https://stats.uptimerobot.com/NLnZ8sVro".toUri())) {
                    Toast.makeText(requireContext(), R.string.no_browser_app_installed_error, Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }

    private fun setupInsultOfTheDayPreferences() {

        with(findPreference<Preference>("iotd_explanation")!!) {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.explanation)
                    .setMessage(R.string.iotd_explanation)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                true
            }
        }

        with(findPreference<SwitchPreferenceCompat>("iotd_master_switch")!!) {
            setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as Boolean
                if (value) {
                    val time = findPreference<TimePickerPreference>("iotd_time")!!.time
                    if (time != null) {
                        InsultOfTheDayBroadcastReceiver.registerDailyInvocation(requireContext(), time)
                    }
                } else {
                    InsultOfTheDayBroadcastReceiver.cancelDailyInvocation(requireContext())
                }
                true
            }
        }
        with(findPreference<TimePickerPreference>("iotd_time")!!) {
            setOnPreferenceChangeListener { _, newValue ->
                val time = newValue as LocalTime?
                if (time == null) {
                    false
                } else {
                    InsultOfTheDayBroadcastReceiver.registerDailyInvocation(requireContext(), time)
                    true
                }
            }
        }

    }

    private fun setupSchedulePeriodicInsultUpdatesPreferences() {
        with(findPreference<EditTextPreference>("number_of_insults_updated_periodically")!!) {
            val maxInsults = 10
            dialogMessage = getString(R.string.how_many_insults_updated_periodically_message, maxInsults)
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val number = (newValue as String).toInt()
                    if (number > maxInsults) {
                        Toast.makeText(requireContext(), getString(R.string.too_many_insults_error, maxInsults), Toast.LENGTH_SHORT).show()
                        return@setOnPreferenceChangeListener false
                    }
                    if (number == 0) {
                        PeriodicInsultUpdatesWorker.cancelUniqueWork(WorkManager.getInstance(requireContext()))
                    } else {
                        val duration = prefs.periodicInsultUpdateInterval.duration
                        PeriodicInsultUpdatesWorker.enqueueUniquePeriodicWork(
                            WorkManager.getInstance(requireContext()),
                            prefs.periodicInsultUpdateConstraints.createWorkManagerConstraints(),
                            duration,
                            number
                        )
                    }
                    true
                } catch (_: NumberFormatException) {
                    Toast.makeText(requireContext(), R.string.format_number_of_insults_error, Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        with(findPreference<ListPreference>("update_insults_periodically_interval")!!) {
            entries = arrayOf(
                resources.getQuantityString(R.plurals.hours, 1, 1),
                resources.getQuantityString(R.plurals.hours, 2, 2),
                resources.getQuantityString(R.plurals.hours, 3, 3),
                resources.getQuantityString(R.plurals.hours, 4, 4),
                resources.getQuantityString(R.plurals.hours, 6, 6),
                resources.getQuantityString(R.plurals.hours, 8, 8)
            )
            setOnPreferenceChangeListener { _, newValue ->
                if (!prefs.isPeriodicInsultUpdatesEnabled()) {
                    return@setOnPreferenceChangeListener true
                }
                val newInterval = PreferencesRepository.UpdateInterval.values()
                    .first { it.value == (newValue as String) }
                val duration = newInterval.duration
                PeriodicInsultUpdatesWorker.enqueueUniquePeriodicWork(
                    WorkManager.getInstance(requireContext()),
                    prefs.periodicInsultUpdateConstraints.createWorkManagerConstraints(),
                    duration,
                    prefs.numberOfInsultsUpdatedPeriodically
                )
                true
            }
        }
        with(findPreference<MultiSelectListPreference>("update_insults_periodically_constraints")!!) {
            setOnPreferenceChangeListener { _, newValue ->
                val prefs = requireContext().myApplication.preferencesRepository
                val numOfInsults = prefs.numberOfInsultsUpdatedPeriodically
                if (!prefs.isPeriodicInsultUpdatesEnabled()) {
                    return@setOnPreferenceChangeListener true
                }
                @Suppress("UNCHECKED_CAST")
                val newConstraints = (newValue as Set<String>)
                    .map { prefValue -> PreferencesRepository.WorkManagerConstraint.values()
                        .first { it.value == prefValue } }
                PeriodicInsultUpdatesWorker.enqueueUniquePeriodicWork(
                    WorkManager.getInstance(requireContext()),
                    newConstraints.createWorkManagerConstraints(),
                    prefs.periodicInsultUpdateInterval.duration,
                    numOfInsults
                )
                true
            }
        }
    }

}