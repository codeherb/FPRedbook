package io.funfun.redbook

sealed class Stream<out T> {

    companion object {
        operator fun <T>invoke(vararg values: () -> T): Stream<T> {
            var results: Stream<T> = None
            for (value in values.reversed()) {
                val newTail = results
                results = Seq( value, { newTail })
            }
            return results
        }
    }
}

data class Seq<T>(private val _head: () -> T, val _tail: () -> Stream<T>): Stream<T>() {
    var head: T? = null
        private set
        get() = field?: _head.invoke().also {
            field = it
        }
    var tail: Stream<T>? = null
        private set
        get() = field?: (_tail.invoke()).also {
            field = it
        }
}
object None: Stream<Nothing>()

fun <T> Stream<T>.head(): T = when {
    this is Seq -> head ?: Unit as T
    else -> Unit as T
}

fun <T> Stream<T>.tail(): Stream<T> = when {
    this is Seq -> tail ?: None
    else -> None
}

fun <T> Stream<T>.filter(acc: Stream<T> = None, predicate: (T) -> Boolean): Stream<T> = when(this) {
    is None -> acc
    is Seq -> if (predicate(head())) {
        head().let {
            tail.filter(Seq({ it }, { acc }), predicate)
        }
    } else {
        tail.filter(acc, predicate)
    }
}

tailrec fun <T> Stream<T>.reverse(acc: () -> Stream<T> = { None }): Stream<T> = when(this) {
    is None -> acc()
    is Seq -> tail().reverse { Seq({ head() }, acc)}
}

// 성능상 이게 좋음, 직관적인 면에서는 foldRight가 좋음
tailrec fun <T, R> Stream<T>.map(acc: (() ->Stream<R>) = { None }, transformer: (T) -> R): Stream<R> = when(this) {
    is None -> acc().reverse()
    is Seq -> tail.map({ Seq({transformer(head())}, acc) }, transformer)
}

tailrec fun <T, R> Stream<T>.foldLeft(z: R, op: (R, T) -> R): R = when(this) {
    is None -> z
    is Seq -> tail().foldLeft(op(z, head()), op)
}

fun <T, R> Stream<T>.foldRight(z: R, op: (T, R) -> R): R = when(this) {
    is None -> z
    is Seq -> op(head(), tail().foldRight(z, op))
}