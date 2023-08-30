package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared

/**
 * The states which any given screen can have.
 */
sealed class ViewState<out T> {
    abstract val data: T

    data object Empty : ViewState<Nothing>() {
        override val data: Nothing get() = error("Unintended use of ${this::class.simpleName}")
    }

    data object Loading : ViewState<Nothing>() {
        override val data: Nothing get() = error("Unintended use of ${this::class.simpleName}")
    }

    data class Content<out T>(override val data: T) : ViewState<T>() {
        override fun toString(): String {
            return "${this::class.simpleName}($data)"
        }
    }

    data class Error<out T>(val exception: Exception) : ViewState<T>() {
        override val data: Nothing get() = error("Unintended use of ${this::class.simpleName}")
        override fun toString(): String {
            return "${this::class.simpleName}($exception)"
        }
    }
}
