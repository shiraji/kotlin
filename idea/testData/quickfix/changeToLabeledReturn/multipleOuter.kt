// "Change to 'return@foo'" "true"
// ACTION: Change to 'return@forEach'
// ACTION: Introduce local variable
// WITH_RUNTIME

fun foo(f:()->Int){}

fun bar() {

    foo {
        listOf(1).forEach {
            return<caret> 1
        }
        return@foo 1
    }
}