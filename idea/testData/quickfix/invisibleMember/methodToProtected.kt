// "Make member visibility 'protected'" "true"

open class A {
    private fun doSth() {
    }
}

class B : A() {
    fun bar() {
        <caret>doSth()
    }
}