// WITH_RUNTIME
// INTENTION_TEXT: Remove @ used as annotation argument

annotation class X(val value: Y, val y: Y)
annotation class Y()

@X(@Y(), y = @Y()<caret>)
fun foo() {
}