// WITH_RUNTIME

import kotlin.test.assertEquals

fun box(): String {
    var digits = 0
    for (i in 1 until 5) {
        digits = digits * 10 + i
    }
    assertEquals(1234, digits)

    return "OK"
}