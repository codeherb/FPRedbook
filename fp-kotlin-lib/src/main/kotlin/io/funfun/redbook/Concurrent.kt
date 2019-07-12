package io.funfun.redbook

//fun <T: Number> sum (ints: List<T>) : Par<T> {
//    return if (ints.size <= 1)
//        Par.unit { ints.getOrElse(0) { 0 as T } }
//    else {
//        val (l, r) = ints.splitAt(ints.size / 2)
//        val sumL = Par.unit { sum(l) }
//        val sumR = Par.unit { sum(r) }
//        // 이런식의 동작은 Par을 get에 넘겨 주는 즉시 완료까지 실행이 차단되는 부수 효과 발생
//        // 연산이 완료되지 않아도 연산을 조합 할 수 있어야 한다.
//        // Par.get(sumL) + Par.get(sumR)
//        Par.map2 (sumL, sumR) { v1, v2 -> v1 + v2 }
//    }
//}
//
//fun <T> List<T>.splitAt(index: Int): Pair<List<T>, List<T>> {
//    return Pair(mutableListOf<T>(), mutableListOf<T>()).apply {
//        this@splitAt.forEachIndexed { i, t ->
//            if (i < index) first.add(t)
//            else second.add(t)
//        }
//    }
//}
//
//sealed class Par <out T> {
//    companion object {
//        fun <T> unit(f: () -> T): Par<T> {
//
//        }
//
//        fun <T> get(a: Par<T>): T {
//
//        }
//
//        fun <T> map2(l: Par<T>, r: Par<T>, f: (v1: T, v2: T) -> T): Par<T> {
//
//        }
//    }
//}