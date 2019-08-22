package io.funfun.redbook

data class State<S, out A>(val run: (S) -> Pair<A, S>) {
    operator fun invoke(state: S) = run(state)

    fun <S> get(): State<S, S> = State {
        Pair(it, it)
    }

    fun <S> set(s: S): State<S, Unit> = State {
        Pair(Unit, s)
    }

    fun <S> modify(f: (S) -> S): State<S, Unit> = State {
        val st = get<S>()(it).second
        set(st)(it)
    }

    companion object {

        fun <S, T> unit(v: T) : State<S, T> = State { s ->
            Pair(v, s)
        }

        fun <S, T, R> flatMap(st: State<S, T>, f: (v: T) -> State<S, R>) : State<S, R> = State { s ->
            val (v,ns) = st(s)
            f(v)(ns)
        }

        fun <S, T, R> map(st: State<S, T>, f: (v: T) -> R) : State<S, R> = State { s ->
            flatMap(st) {
                unit<S, R>(f(it))
            } (s)
        }

        fun <S, A, B, C> map2(sa: State<S, A>, sb: State<S, B>, f: (A, B) -> C): State<S, C> = State { s ->
            flatMap(sa) { a ->
                flatMap(sb) { b ->
                    unit<S, C>(f(a, b))
                }
            } (s)
        }

        fun <S, A> sequence(fs: List<State<S, A>>): State<S, List<A>> = State { s ->
            fs.foldRight(unit<S, List<A>>(listOf())) { st, acc ->
                map2(st, acc) { a, b ->
                    mutableListOf(a).apply { addAll(b) }
                }
            } (s)
        }

    }
}

