// WITH_RUNTIME

val cmin = Character.MIN_VALUE
val bmin = Byte.MIN_VALUE
val smin = Short.MIN_VALUE
val imin = Int.MIN_VALUE
val lmin = Long.MIN_VALUE

fun box(): String {
    var exns = 0

    try {
        for (i in 0 until imin) {
            throw AssertionError("'until' should throw IllegalArgumentException")
        }
        throw AssertionError("'until' should throw IllegalArgumentException")
    }
    catch (e: IllegalArgumentException) {
        exns++
    }
    catch (e: Exception) {
        return "Fail: $e"
    }

    try {
        for (i in cmin until cmin) {
            throw AssertionError("'until' should throw IllegalArgumentException")
        }
        throw AssertionError("'until' should throw IllegalArgumentException")
    }
    catch (e: IllegalArgumentException) {
        exns++
    }
    catch (e: Exception) {
        return "Fail: $e"
    }

    try {
        for (i in bmin until bmin) {
            throw AssertionError("'until' should throw IllegalArgumentException")
        }
        throw AssertionError("'until' should throw IllegalArgumentException")
    }
    catch (e: IllegalArgumentException) {
        exns++
    }
    catch (e: Exception) {
        return "Fail: $e"
    }

    try {
        for (i in smin until smin) {
            throw AssertionError("'until' should throw IllegalArgumentException")
        }
        throw AssertionError("'until' should throw IllegalArgumentException")
    }
    catch (e: IllegalArgumentException) {
        exns++
    }
    catch (e: Exception) {
        return "Fail: $e"
    }

    try {
        for (i in lmin until lmin) {
            throw AssertionError("'until' should throw IllegalArgumentException")
        }
        throw AssertionError("'until' should throw IllegalArgumentException")
    }
    catch (e: IllegalArgumentException) {
        exns++
    }
    catch (e: Exception) {
        return "Fail: $e"
    }

    return if (exns == 5) "OK" else "Fail: exns=$exns"
}