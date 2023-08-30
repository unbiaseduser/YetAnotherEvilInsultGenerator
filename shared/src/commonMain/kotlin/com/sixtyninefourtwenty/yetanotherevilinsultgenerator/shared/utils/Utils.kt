package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.utils

fun <T> MutableCollection<T>.resetTo(elem: T) {
    clear()
    add(elem)
}

fun <T> MutableCollection<T>.resetTo(elems: Iterable<T>) {
    clear()
    addAll(elems)
}

fun Throwable.asExceptionOrThrow() = if (this is Exception) this else throw this