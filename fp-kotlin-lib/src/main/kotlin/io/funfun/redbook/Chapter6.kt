package io.funfun.redbook

import io.funfun.redbook.RandImpl.both
import io.funfun.redbook.RandImpl.nonNegativeLessThan

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

typealias Rand<A> = (RNG) -> Pair<A, RNG>

class SimpleRng(val seed: Long): RNG {

    override fun nextInt(): Pair<Int, RNG> {
        val newSeed = (seed * 0x5DEECE66DL + 0xBL) and 0xFFFFFFFFFFFFL
        val nextRNG = SimpleRng(newSeed)
        val n = (newSeed ushr 16).toInt()
        return Pair(n, nextRNG)
    }

}

// 0 이상 MAX_VALUE 미만 정수 반환
fun nonNegativeInt(rng: RNG): Pair<Int, RNG> {
    val (num, newRng) = rng.nextInt()
    return if (num > 0)
            Pair(num, newRng)
        else if (num == Int.MIN_VALUE)
            Pair(-(num + 1), newRng)
        else
            Pair(-num, newRng)
}

// 0 이상 1미만의 double 반환
fun double(rng: RNG): Pair<Double, RNG> {
    val (num, newRng) = nonNegativeInt(rng)
    return Pair(num / (Int.MAX_VALUE.toDouble() + 1), newRng)
}

fun intDouble(rng: RNG) : Pair<Pair<Int, Double>, RNG> {
    val (numInt, newRng) = rng.nextInt()
    val (numDouble, newReg2) = double(newRng)
    return Pair(Pair(numInt, numDouble), newReg2)
}

fun doubleInt(rng: RNG) : Pair<Pair<Double, Int>, RNG> {
    val (numDouble, newReg) = double(rng)
    val (numInt, newRng2) = newReg.nextInt()
    return Pair(Pair(numDouble, numInt), newRng2)
}

// double 3튜플 반환
fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
    val (numDouble1, newReg) = double(rng)
    val (numDouble2, newReg1) = double(newReg)
    val (numDouble3, newReg2) = double(newReg1)
    return Pair(Triple(numDouble1, numDouble2, numDouble3), newReg2)
}

// 정수 난수들의 목록을 생성하는 함수를 작성하라.
fun ints(count: Int, rng: RNG, acc: List<Int> = listOf()): Pair<List<Int>, RNG> =
        if (count == 0) Pair(acc, rng)
        else {
            val (num, newRng) = rng.nextInt()
            ints(count - 1, newRng, mutableListOf(num).apply { addAll(acc) })
        }

object RandImpl {

    fun <A> unit(a: A): Rand<A> = { Pair(a, it) }

    fun <A, B> _map(s: Rand<A>, f: (A) -> B): Rand<B> =
            { s(it).let { (v, rng) -> Pair(f(v), rng) } }

    fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> = { rng ->
        flatMap(s) {
            unit(f(it))
        } (rng)
    }

    fun <A, B, C> _map2(ra: Rand<A>, rb: Rand<B>, f: ((A, B) -> C)) : Rand<C> =
            { ra(it).let { (a, rng) ->
                rb(rng).let { (b, rng2) ->
                    Pair(f(a, b), rng2)
                }
            }}

    fun <A, B, C> map2(ra: Rand<A>, rb: Rand<B>, f: ((A, B) -> C)) : Rand<C> = { rng ->
        flatMap(ra) { a ->
            flatMap(rb) { b ->
                unit(f(a, b))
            }
        } (rng)
    }


    fun <T, R> flatMap(ra: Rand<T>, f: (v: T) -> Rand<R>) : Rand<R> {
        return {
            val (v, rng) = ra(it)
            f(v)(rng)
        }
    }

    fun <A, B> both(ra: Rand<A>, rb: Rand<B>): Rand<Pair<A, B>> =
            map2(ra, rb) { a, b -> Pair(a, b) }

    fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
            {
                val acc = mutableListOf<A>()
                var next = it
                fs.forEach { rand ->
                    val (v, rng) = rand(next)
                    acc.add(v)
                    next = rng
                }
                Pair(acc, next)
            }

    fun <A> sequence1(fs: List<Rand<A>>): Rand<List<A>> =
            fs.foldRight(unit(listOf())) { rnd, acc ->
                map2(rnd, acc) { a, b -> mutableListOf(a).apply { addAll(b) } }
            }


    fun nonNegativeLessThan0(n: Int): Rand<Int> = {
        val (i, rng2) = nonNegativeInt(it)
        val mod = i % n
        if (i + (n - 1) - mod >= 0)
            Pair(mod, rng2)
        else
            nonNegativeLessThan0(n)(rng2)
    }

    fun nonNegativeLessThan(n: Int): Rand<Int> = { rng ->
        flatMap(::nonNegativeInt) { v ->
            val mod = v % n
            if (v + (n - 1) - mod >= 0) unit(mod) else nonNegativeLessThan(n)
        } (rng)
    }
}

val int: Rand<Int> = { it.nextInt() }

val double: Rand<Double> = RandImpl.map(int) { it / (Int.MAX_VALUE.toDouble() + 1) }

fun <A> nonNegativeEven(s: Rand<Int>): Rand<Int> =
        RandImpl.map(s) { it - it % 2 }

val randIntDouble: Rand<Pair<Int, Double>> =
        both(int, double)

val randDoubleInt: Rand<Pair<Double, Int>> =
        both(double, int)

val rollDie: Rand<Int> = RandImpl.map(nonNegativeLessThan(6)) { it + 1 }

sealed class Input
object Coin: Input()
object Turn: Input()

data class Machine(val locked: Boolean, val candies: Int, val coins: Int)

/**
 * Vending Machine Role
 *
 * 1. Given : 잠겨진 자판기 && 사탕이 있음  When : 동전을 넣으면 then : 잠금이 풀린다.
 * 2. Given : 잠금이 풀린 자판기 When : 손잡이 돌리면 Then : 사탕이 나오고, 잠긴다.
 * 3. Given : 잠겨진 자판기 When : 손잡이 돌리면 Then : 아무일 없음
 * 3-1 Given : 잠금 풀린 자판기 When : 동전을 넣으면 Then : 아무일 없음
 * 4. Given : 사탕이 없는 자판기 When : 모든 입력 Then : 아무일 없음
 */

fun simulateMachine(inputs: List<Input>): State<Machine, Pair<Int, Int>> = State {
    inputs.fold(State.unit<Machine, Pair<Int, Int>>(Pair(0, 0))) { result, event ->
        State.flatMap(result) { (coin, candy) ->
            State.map(result) {
                when(event) {
                    is Coin -> Pair(coin + 1, candy)
                    is Turn -> Pair(coin, candy - 1)
                }
            }
        }
    } (it)
}