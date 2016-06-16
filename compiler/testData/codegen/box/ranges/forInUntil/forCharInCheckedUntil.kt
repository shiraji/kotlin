// WITH_RUNTIME

import kotlin.test.assertEquals

fun box(): String {
    var chars = ""
    for (ch in 'a' until 'd') {
        chars = "$chars$ch"
    }
    assertEquals("abc", chars)

    return "OK"
}