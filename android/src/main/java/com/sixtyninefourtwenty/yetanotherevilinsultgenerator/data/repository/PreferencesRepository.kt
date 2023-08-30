package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.AndroidPreferences
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.Preferences
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.isDeviceOnOrOverSdk
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.prefValue
import java.time.Duration

class PreferencesRepository(context: Context) : AndroidPreferences(context) {

    var savedIotdInsult: Insult?
        get() = getStringOrNull("insult")?.let { Insult.fromJson(it) }
        set(value) { putStringOrNull("insult", value?.toJson()) }

    var generatorLanguage: Language
        get() = Language.values().first { it.languageCode == getString("generator_language", Language.ENGLISH.languageCode) }
        set(value) { putString("generator_language", value.languageCode) }

    var generatorUrl: OfficialOnlineInsultGeneratorService.Url
        get() = OfficialOnlineInsultGeneratorService.Url.values().first { it.prefValue == getString("generator_url", OfficialOnlineInsultGeneratorService.Url.MAIN.prefValue) }
        set(value) { putString("generator_url", value.prefValue) }

    enum class WorkManagerConstraint(override val value: String) : Preferences.StringValue {
        UNMETERED_NETWORK("unmetered_network"),
        DEVICE_CHARGING("device_charging"),
        DEVICE_IDLE("device_idle"),
        BATTERY_NOT_LOW("battery_not_low"),
        STORAGE_NOT_LOW("storage_not_low")
    }

    var iotdWorkManagerConstraints: Iterable<WorkManagerConstraint>
        get() = WorkManagerConstraint.values().getStringValueEnums("iotd_constraints")
        set(value) { putStringValueEnums("iotd_constraints", value) }

    enum class UpdateInterval(override val value: String, val duration: Duration) : Preferences.StringValue {
        ONE_HOUR("1h", Duration.ofHours(1)),
        TWO_HOURS("2h", Duration.ofHours(2)),
        THREE_HOURS("3h", Duration.ofHours(3)),
        FOUR_HOURS("4h", Duration.ofHours(4)),
        SIX_HOURS("6h", Duration.ofHours(6)),
        EIGHT_HOURS("8h", Duration.ofHours(8))
    }

    var numberOfInsultsUpdatedPeriodically: Int
        get() = getString("number_of_insults_updated_periodically", "0").toInt()
        set(value) { putString("number_of_insults_updated_periodically", value.toString()) }

    fun isPeriodicInsultUpdatesEnabled() = numberOfInsultsUpdatedPeriodically > 0

    var periodicInsultUpdateInterval: UpdateInterval
        get() = UpdateInterval.values().getStringValueEnum("update_insults_periodically_interval", UpdateInterval.ONE_HOUR)
        set(value) { putStringValueEnum("update_insults_periodically_interval", value) }

    var periodicInsultUpdateConstraints: Iterable<WorkManagerConstraint>
        get() = WorkManagerConstraint.values().getStringValueEnums("update_insults_periodically_constraints")
        set(value) { putStringValueEnums("update_insults_periodically_constraints", value) }

    fun performMigrations() {
        val prefValue = getString("generator_url", "main")
        val replacement = when (prefValue) {
            "https://evilinsult.com/" -> "main"
            "https://slave.evilinsult.com/" -> "backup"
            else -> prefValue
        }
        if (prefValue != replacement) {
            putString("generator_url", replacement)
        }
    }

    companion object {
        fun initializeInitialValues(context: Context) {
            PreferenceManager.setDefaultValues(context, R.xml.preferences_root, false)
        }
    }

}

fun Iterable<PreferencesRepository.WorkManagerConstraint>.createWorkManagerConstraints(isNetworkRequired: Boolean = true) = Constraints.Builder().apply {
    if (isNetworkRequired) {
        if (contains(PreferencesRepository.WorkManagerConstraint.UNMETERED_NETWORK)) {
            setRequiredNetworkType(NetworkType.UNMETERED)
        } else {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }
    }
    if (contains(PreferencesRepository.WorkManagerConstraint.DEVICE_CHARGING)) {
        setRequiresCharging(true)
    }
    if (contains(PreferencesRepository.WorkManagerConstraint.DEVICE_IDLE) && isDeviceOnOrOverSdk(Build.VERSION_CODES.M)) {
        setRequiresDeviceIdle(true)
    }
    if (contains(PreferencesRepository.WorkManagerConstraint.BATTERY_NOT_LOW)) {
        setRequiresBatteryNotLow(true)
    }
    if (contains(PreferencesRepository.WorkManagerConstraint.STORAGE_NOT_LOW)) {
        setRequiresStorageNotLow(true)
    }
}.build()
