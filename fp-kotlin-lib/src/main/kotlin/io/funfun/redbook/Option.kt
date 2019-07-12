package io.funfun.redbook

sealed class Option<out T> {
    data class Some<T>(val get: T): Option<T>()
    object None: Option<Nothing>()
}
