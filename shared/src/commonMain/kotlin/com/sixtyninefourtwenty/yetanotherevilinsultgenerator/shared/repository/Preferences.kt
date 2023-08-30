package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository

@Suppress("unused")
interface Preferences {

    interface IntValue {
        val value: Int
    }

    fun <E> Array<E>.getIntValueEnum(key: String, defValue: E): E where E : IntValue, E : Enum<E> {
        val prefValue = getInt(key, defValue.value)
        return first { it.value == prefValue }
    }

    fun <E> putIntValueEnum(key: String, value: E) where E : IntValue, E : Enum<E> {
        putInt(key, value.value)
    }

    interface FloatValue {
        val value: Float
    }

    interface LongValue {
        val value: Long
    }

    interface StringValue {
        val value: String
    }

    fun <E> Array<E>.getStringValueEnum(key: String, defValue: E): E where E : StringValue, E : Enum<E> {
        val prefValue = getString(key, defValue.value)
        return first { it.value == prefValue }
    }

    fun <E> putStringValueEnum(key: String, value: E) where E : StringValue, E : Enum<E> {
        putString(key, value.value)
    }

    fun <E> Array<E>.getStringValueEnums(key: String, defValue: Iterable<E> = setOf()): Iterable<E> where E : StringValue, E : Enum<E> {
        val prefValues = getStrings(key, defValue.map { it.value })
        return prefValues.mapTo(createEnumCollection(get(0))) { prefValue ->
            first { it.value == prefValue }
        }
    }

    fun <E> putStringValueEnums(key: String, value: Iterable<E>) where E : StringValue, E : Enum<E> {
        putStrings(key, value.map { it.value })
    }

    fun getInt(key: String, defValue: Int): Int
    fun getFloat(key: String, defValue: Float): Float
    fun getLong(key: String, defValue: Long): Long
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun getString(key: String, defValue: String): String
    fun getStringOrNull(key: String): String?
    fun getStrings(key: String, defValue: Iterable<String>): Iterable<String>

    fun putInt(key: String, value: Int)
    fun putFloat(key: String, value: Float)
    fun putLong(key: String, value: Long)
    fun putBoolean(key: String, value: Boolean)
    fun putString(key: String, value: String)
    fun putStringOrNull(key: String, value: String?)
    fun putStrings(key: String, value: Iterable<String>)
}

expect fun <E : Enum<E>> createEnumCollection(obj: E): MutableCollection<E>
