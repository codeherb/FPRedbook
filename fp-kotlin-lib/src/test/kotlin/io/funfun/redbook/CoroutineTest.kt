package io.funfun.redbook

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test

class CoroutineTest {

    @Test
    fun testGlobalScope() {
        GlobalScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000L)
                println("I'm working")
            }
        }
    }
}