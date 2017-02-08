// WITH_RUNTIME

class MyClass {
    fun foo1() = Unit
    fun foo2() = "FOO"
    fun foo3() = Unit

    fun foo4(a: MyClass) {
        a.foo1()<caret>
        a.foo2().toString()
        a.foo3()
    }
}