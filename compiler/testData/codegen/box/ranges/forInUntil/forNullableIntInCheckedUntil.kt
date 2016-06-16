// WITH_RUNTIME

import kotlin.test.assertEquals

fun suppressBoxingOptimization(ni: Int?) {}

fun box(): String {
    var digits = 0
    for (i: Int? in 1 until 5) {
        suppressBoxingOptimization(i)
        digits = digits * 10 + i!!
    }
    assertEquals(1234, digits)

    return "OK"
}