package io.funfun.redbook

sealed class Stream<out T> {

    companion object {
        operator fun <T>invoke(vararg values: () -> T): Stream<T> {
            var results: Stream<T> = None
            for (value in values.reversed()) {
                results = Seq(value, results)
            }
            return results
        }
    }
}

data class Seq<T>(private val _head: () -> T, val tail: Stream<T>): Stream<T>() {
    var head: T? = null
        private set
        get() = field?: _head.invoke().also {
            field = it
        }

}
object None: Stream<Nothing>()

fun <T> Stream<T>.head() = when {
    this is Seq -> head
    else -> throw Exception("This is Empty Stream")
}

fun <T> Stream<T>.tail() = when {
    this is Seq -> tail
    else -> throw Exception("This is Empty Stream")
}