/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package io.funfun.redbook

import kotlin.test.Test
import kotlin.test.assertNotNull
import org.scalatest.{FunSuite, TestSuite}

class AppTest extends FunSuite {

  test("A FPList should not null and size 6") {
    val list = FPList(1,2,3,4,5,6)
    assertResult(6)(list.length())
  }

  test("FP리스트의 3번째에는 3값이 있어야 한다.") {
    val list = FPList(1,2,3,4,5,6)
    assertResult(3)(head(tail(tail(list))))
  }

}
