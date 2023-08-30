package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository

import java.util.prefs.Preferences

open class JavaPreferences(clazz: Class<*>) :
    com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.Preferences {

    private val preferences: Preferences = Preferences.userNodeForPackage(clazz)

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
        return preferences.get(key, defValue)
    }

    override fun getStringOrNull(key: String): String? {
        return preferences.get(key, null)
    }

    override fun getStrings(key: String, defValue: Iterable<String>): Iterable<String> {
        return preferences.get(key, null)?.split(",") ?: defValue
    }

    override fun putInt(key: String, value: Int) {
        preferences.putInt(key, value)
    }

    override fun putFloat(key: String, value: Float) {
        preferences.putFloat(key, value)
    }

    override fun putLong(key: String, value: Long) {
        preferences.putLong(key, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences.putBoolean(key, value)
    }

    override fun putString(key: String, value: String) {
        preferences.put(key, value)
    }

    override fun putStringOrNull(key: String, value: String?) {
        preferences.put(key, value)
    }

    override fun putStrings(key: String, value: Iterable<String>) {
        preferences.put(key, value.joinToString(","))
    }

}