package io.funfun.redbook

import java.lang.Exception

sealed class Either <out E, out A> {
    data class Left<out E> (val value: E) : Either<E, Nothing>()
    data class Right<out A> (val value: A) : Either<Nothing, A>()
}

fun <E, A, B> Either<E, A>.map(f: (A) -> B): Either<E, B> = when(this) {
    is Either.Right -> Either.Right(f(value))
    is Either.Left -> this
}

fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> = when(this) {
    is Either.Right -> f(value)
    is Either.Left -> this
}

fun <E, A: B, B> Either<E, A>.orElse(b: () -> Either<E, B>): Either<E, B> = when(this) {
    is Either.Right -> this
    is Either.Left -> b()
}

// 다음 동작을 일반화 하면 map2와 같이 표현 할 수 있다.
//when(this) {
//    is Either.Right -> {
//        when (b) {
//            is Either.Right -> Either.Right(f(value, b.value))
//            is Either.Left -> b
//        }
//    }
//    is Either.Left -> this
//}
fun <E, A, B, C> Either<E, A>.map2(b: Either<E, B>, f: (A, B) -> C): Either<E, C> =
    this.flatMap { aa -> b.map { bb -> f(aa, bb) } }

fun <A> Try(a: () -> A): Either<Exception, A> =
        try {
            Either.Right(a())
        } catch (e: Exception) {
            Either.Left(e)
        }

fun safeDiv(x: Int, y: Int): Either<Exception, Int> =
        Try { x / y }