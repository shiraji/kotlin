// "Change 'y' to '*y'" "true"
// DISABLE-ERRORS

fun foo(vararg x: String) {}

fun bar(y: Array<String>) = foo(y<caret>)
