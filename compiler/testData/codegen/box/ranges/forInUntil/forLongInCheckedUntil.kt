// WITH_RUNTIME

import kotlin.test.assertEquals

fun box(): String {
    var digits = 0L
    for (i in 1L until 5L) {
        digits = digits * 10L + i
    }
    assertEquals(1234L, digits)

    return "OK"
}