// "Change 'array' to '*array'" "true"
// DISABLE-ERRORS

fun foo(a: String, vararg x: String, b: Int) {}

fun bar(array: Array<String>) = foo("aaa", array<caret>, 1)
