package io.funfun.redbook

sealed class Option<out T> {
    data class Some<T>(val get: T): Option<T>()
    object None: Option<Nothing>()
}

fun <A, B> Option<A>.map(f: (A) -> B): Option<B> = when(this) {
    is Option.Some -> Option.Some(f(get))
    is Option.None -> this
}

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> = when(this) {
    is Option.Some -> f(get)
    is Option.None -> this
}

fun <A: B, B> Option<A>.getOrElse(default: () -> B): B = when(this) {
    is Option.Some -> get
    is Option.None -> default()
}

fun <A: B, B> Option<A>.OrElse(ob: () -> Option<B>): Option<B> = when(this) {
    is Option.Some -> this
    is Option.None -> ob()
}

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = when(this) {
    is Option.None -> this
    is Option.Some -> if (f(get)) this else Option.None
}

fun mean(xs: List<Double>): Option<Double> =
        if (xs.isEmpty()) Option.None
        else Option.Some(xs.sum() / xs.size)
