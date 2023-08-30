package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

open class AndroidPreferences(private val preferences: SharedPreferences) : Preferences {

    constructor(context: Context) : this(PreferenceManager.getDefaultSharedPreferences(context))

    override fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    override fun getString(key: String, defValue: String): String {
        return preferences.getString(key, defValue)!!
    }

    override fun getStringOrNull(key: String): String? {
        return preferences.getString(key, null)
    }

    override fun getStrings(key: String, defValue: Iterable<String>): Iterable<String> {
        return preferences.getStringSet(key, defValue.toSet())!!
    }

    override fun putInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    override fun putFloat(key: String, value: Float) {
        preferences.edit { putFloat(key, value) }
    }

    override fun putLong(key: String, value: Long) {
        preferences.edit { putLong(key, value) }
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences.edit { putBoolean(key, value) }
    }

    override fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    override fun putStringOrNull(key: String, value: String?) {
        preferences.edit { putString(key, value) }
    }

    override fun putStrings(key: String, value: Iterable<String>) {
        preferences.edit { putStringSet(key, value.toSet()) }
    }

}
