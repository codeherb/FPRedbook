package io.funfun.redbook

import kotlinx.coroutines.*

// 포인트1 : 계산의 서술과 실행의 분리
// 포인트2 : 부수효과를 허용하지 않아야함
// 포인트3 : 합성 가능해야 한다.

// 설계방법 : 처리하고자 하는 사용 예에서 시작 -> 인터페이스 개발 -> 인터페이스 구현 방법 고민

// 일반적인 sum 연산
fun sum(ints: Stream<Int>): Int =
        ints.foldLeft(0) { v1, v2 -> v1 + v2 }

// 분할 정복 알고리즘을 이용한 합산
// sum(l) 이나 sum(r)같은 하나의 병렬 계산을 나타내는 자료구조는
// 하나의 결과를 담을 수 있어야 하며, 원하는 시기에 계산을 추출 가능해야함
fun sumD (ints: List<Int>): Int =
        if (ints.size <= 1)
            ints.getOrElse(0) { 0 }
        else {
            val (l, r) = ints.splitAt(ints.size / 2)
            sumD(l) + sumD(r)
        }

fun sumP (ints: List<Int>) : Par<Int> {
    return if (ints.size <= 1)
        unit(ints.getOrElse(0) { 0 })
    else {
        val (l, r) = ints.splitAt(ints.size / 2)
//        val sumL = lazyUnit { sumP(l) }
//        val sumR = lazyUnit { sumP(r) }
        val sumL = sumP(l)
        val sumR = sumP(r)
        // 이런식의 동작은 Par을 get에 넘겨 주는 즉시 완료까지 실행이 차단되는 부수 효과 발생
        // 연산이 완료되지 않아도 연산을 조합 할 수 있어야 한다.
        // Par.get(sumL) + Par.get(sumR)
        // 쓰레드를 뛰워야 할지 말아야할지에 대해 명확한 정의를 위해 fork 도입
        map2(sumL, sumR) { v1: Int, v2:Int -> v1 + v2 }
    }
}

fun <T> List<T>.splitAt(index: Int): Pair<List<T>, List<T>> {
    return Pair(mutableListOf<T>(), mutableListOf<T>()).apply {
        this@splitAt.forEachIndexed { i, t ->
            if (i < index) first.add(t)
            else second.add(t)
        }
    }
}

// 코루틴을 사용하는 비동기 api
typealias Par<A> = (suspend (CoroutineScope) -> Deferred<A>)

// 즉시 평가되어 결과 a를 산출하는 계산을 생성
fun <T> unit(a: T): Par<T> = { _: CoroutineScope -> CompletableDeferred(a) }

// run이 동시적으로 평가한 표현식 a를 감싼다.
fun <T> lazyUnit(a: () -> T): Par<T> = fork { unit(a()) }

// 두 병렬 계산의 결과를 이항 함수로 조합함
fun <A, B, C> map2(l: Par<A>, r: Par<B>, f: (v1: A, v2: B) -> C): Par<C> =
        { s: CoroutineScope ->
            val lf = l(s).await()
            val rf = r(s).await()
            unit(f(lf, rf))(s)
        }

// 인수들이 개별 스레드에서 평가되도록 한다.
// 평가를 fork의 책임으로 둘것인가? get의 책임으로 돌릴 것인가?
// fork의 책임으로 둔다면 api 사용전에 글로벌한 위치에서 초기화가 이루어져있어야만 한다.
// 프로그래머가 병렬 평가 전략을 결정할 수 없다. - 평가를 get의 역할로 두는 것이 적절해보인다.
// fork는 병렬처리 해야한다는 표시만 남기도록 하자.
fun <T> fork(a: () -> Par<T>): Par<T> =
        { s: CoroutineScope ->
            s.async { a()(s).await() }
        }

fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> =
        { p ->
            lazyUnit { f(p) }
        }

// get 에서 run 으로 이름을 바꾸어 병렬성의 실제 구현 지점임을 표현하자
// 주어진 Par를 fork의 요청에 따라 병렬 계산을 수행하고 그 결과를 얻어 완전 평가 한다.
fun <T> run(a: Par<T>): T = runBlocking {
    a.invoke(this).await()
}

fun sortPar(parList: Par<List<Int>>): Par<List<Int>> =
        map2(parList, unit(listOf<Int>())){ a, _ ->  a.sorted() }

fun <A, B> map(pa: Par<A>, f: (A) -> B): Par<B> =
        map2(pa, unit(Unit)) { a, _ -> f(a) }

