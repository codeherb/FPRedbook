package io.funfun.redbook

sealed class FPList<out T> {
    companion object {

        operator fun <T> invoke(vararg values: T): FPList<T> {
            var results: FPList<T> = None
            for (value in values.reversed()) {
                results = Cons(value, results)
            }
            return results
        }

    }

    data class Cons<T>(val head: T, val tail: FPList<T>): FPList<T>()
    object None: FPList<Nothing>()
}

fun <T> FPList<T>.filter(acc: FPList<T> = FPList.None, predicate: (v: T) -> Boolean): FPList<T> = when(this) {
    is FPList.None -> acc
    is FPList.Cons -> if (predicate(head)) {
        FPList.Cons(head, acc)
    } else {
        acc
    }
}

fun <T, R> FPList<T>.map(acc: FPList<R> = FPList.None, func: (v: T) -> R): FPList<R> = when(this) {
    is FPList.None -> acc.reverse()
    is FPList.Cons -> tail.map(FPList.Cons(func(head), acc), func)
}

fun <T, R> FPList<T>.flatMap(acc: FPList<R> = FPList.None, func: (v: T) -> FPList<R>): FPList<R> = when(this) {
    is FPList.None -> acc
    is FPList.Cons -> tail.flatMap(acc.append(func(head)), func)
}

fun <T> FPList<T>.reverse(acc: FPList<T> = FPList.None): FPList<T> = when(this) {
    is FPList.None -> acc
    is FPList.Cons -> tail.reverse(FPList.Cons(head, acc))
}

fun <T> FPList<FPList<T>>.flatten(acc: FPList<T> = FPList.None): FPList<T> = when(this) {
    is FPList.None -> acc
    is FPList.Cons -> tail.flatten(head.append(acc))
}

fun <T> FPList<T>.append(other: FPList<T>): FPList<T> = when(this) {
    is FPList.None -> other
    is FPList.Cons -> if (tail !== FPList.None) {
        FPList.Cons(head, tail.append(other))
    } else {
        FPList.Cons(head, other)
    }
}
