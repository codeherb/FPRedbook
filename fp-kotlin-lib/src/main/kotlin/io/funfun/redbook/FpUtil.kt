package io.funfun.redbook

fun <A, B, C> partial1(a: A, f: (A, B) -> C): (B) -> C =
        { b: B -> f(a, b) }

fun <A, B, C> curry(f: (A, B) -> C) : (A) -> ((B) -> C) =
        { a: A ->
            { b: B -> f(a, b) }
        }

fun <A, B, C> uncurry(f: (A) -> ((B) -> C)) : (A, B) -> C =
        { a: A, b: B ->
            f(a)(b)
        }

fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C =
        { a: A ->
            f(g(a))
        }